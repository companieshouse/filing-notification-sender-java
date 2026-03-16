package uk.gov.companieshouse.filing.common.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.retrytopic.RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS;
import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.regex.Pattern;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.common.exception.RetryableException;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class LoggingKafkaListenerAspectTest {

    private static final String TOPIC = "filing-processed";

    private static final Pattern INFO_EVENT_PATTERN = Pattern.compile(
            "event: info|\"event\":\"info\"");
    private static final Pattern ERROR_EVENT_PATTERN = Pattern.compile(
            "event: error|\"event\":\"error\"");
    private static final Pattern MAX_RETRY_ATTEMPTS_REACHED_PATTERN = Pattern.compile(
            "error: Max retry attempts reached|\"message\":\"Max retry attempts reached\"");
    private static final Pattern INVALID_PAYLOAD_PATTERN = Pattern.compile(
            "error: Invalid payload type, payload: \\[message payload]|\"message\":\"Invalid payload type, payload: \\[message payload]\"");
    private static final Pattern REQUEST_ID_INITIALISED_PATTERN = Pattern.compile(
            "request_id: filing-processed-0-0|\"request_id\":\"filing-processed-0-0\"");
    private static final Pattern RETRY_COUNT_ZERO_PATTERN = Pattern.compile(
            "retry_count: 0|\"retry_count\":0");
    private static final Pattern RETRY_COUNT_FOUR_PATTERN = Pattern.compile(
            "retry_count: 4|\"retry_count\":4");
    private static final Pattern MAIN_TOPIC_PATTERN = Pattern.compile(
            "topic: filing-processed|\"topic\":\"filing-processed\"");
    private static final Pattern PARTITION_ZERO_PATTERN = Pattern.compile(
            "partition: 0|\"partition\":0");
    private static final Pattern OFFSET_ZERO_PATTERN = Pattern.compile(
            "offset: 0|\"offset\":0");
    private static final String TRANSACTION_ID = "123456789";
    private static final Pattern TRANSACTION_ID_PATTERN = Pattern.compile(
            "transaction_id: %s|\"transaction_id\":\"%s\"".formatted(TRANSACTION_ID, TRANSACTION_ID));

    private LoggingKafkaListenerAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Message<FilingReceived> filingReceivedMessage;
    @Mock
    private FilingReceived filingReceived;
    @Mock
    private SubmissionRecord filingReceivedSubmission;

    @Mock
    private Message<FilingProcessed> filingProcessedMessage;
    @Mock
    private FilingProcessed filingProcessed;
    @Mock
    private uk.gov.companieshouse.filing.processed.SubmissionRecord filingProcessedSubmission;

    @Mock
    private Message<String> invalidMessage;

    @BeforeEach
    void setUp() {
        aspect = new LoggingKafkaListenerAspect(5);
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldManageFilingReceivedStructuredLogging(CapturedOutput capture) throws Throwable {
        // given
        MessageHeaders headers = new MessageHeaders(
                Map.of(
                        RECEIVED_TOPIC, TOPIC,
                        RECEIVED_PARTITION, 0,
                        OFFSET, 0L));
        Object expected = "result";
        when(filingReceivedMessage.getHeaders()).thenReturn(headers);
        when(joinPoint.getArgs()).thenReturn(new Object[]{filingReceivedMessage});
        when(filingReceivedMessage.getPayload()).thenReturn(filingReceived);
        when(filingReceived.getSubmission()).thenReturn(filingReceivedSubmission);
        when(filingReceivedSubmission.getTransactionId()).thenReturn(TRANSACTION_ID);
        when(joinPoint.proceed()).thenReturn(expected);

        // when
        Object actual = aspect.manageStructuredLogging(joinPoint);

        //then
        assertEquals(expected, actual);
        assertTrue(capture.getOut().contains("Processing message"));
        assertTrue(capture.getOut().contains("Processed message"));
        verifyInfoLogMap(capture);
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldManageFilingProcessedStructuredLogging(CapturedOutput capture) throws Throwable {
        // given
        MessageHeaders headers = new MessageHeaders(
                Map.of(
                        RECEIVED_TOPIC, TOPIC,
                        RECEIVED_PARTITION, 0,
                        OFFSET, 0L));
        Object expected = "result";
        when(filingProcessedMessage.getHeaders()).thenReturn(headers);
        when(joinPoint.getArgs()).thenReturn(new Object[]{filingProcessedMessage});
        when(filingProcessedMessage.getPayload()).thenReturn(filingProcessed);
        when(filingProcessed.getSubmission()).thenReturn(filingProcessedSubmission);
        when(filingProcessedSubmission.getTransactionId()).thenReturn(TRANSACTION_ID);
        when(joinPoint.proceed()).thenReturn(expected);

        // when
        Object actual = aspect.manageStructuredLogging(joinPoint);

        //then
        assertEquals(expected, actual);
        assertTrue(capture.getOut().contains("Processing message"));
        assertTrue(capture.getOut().contains("Processed message"));
        verifyInfoLogMap(capture);
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldLogInfoWhenRetryableException(CapturedOutput capture) throws Throwable {
        // given
        MessageHeaders headers = new MessageHeaders(
                Map.of(
                        RECEIVED_TOPIC, TOPIC,
                        RECEIVED_PARTITION, 0,
                        OFFSET, 0L));

        when(joinPoint.getArgs()).thenReturn(new Object[]{filingReceivedMessage});
        when(filingReceivedMessage.getPayload()).thenReturn(filingReceived);
        when(filingReceivedMessage.getHeaders()).thenReturn(headers);
        when(filingReceived.getSubmission()).thenReturn(filingReceivedSubmission);
        when(filingReceivedSubmission.getTransactionId()).thenReturn(TRANSACTION_ID);
        when(joinPoint.proceed()).thenThrow(RetryableException.class);

        // when
        Executable actual = () -> aspect.manageStructuredLogging(joinPoint);

        //then
        assertThrows(RetryableException.class, actual);
        assertTrue(capture.getOut().contains("Retryable exception thrown"));
        verifyInfoLogMap(capture);
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldLogInfoWhenRetryableExceptionMaxAttempts(CapturedOutput capture) throws Throwable {
        // given
        MessageHeaders headers = new MessageHeaders(
                Map.of(
                        // attempt header returns a byte array not an integer
                        DEFAULT_HEADER_ATTEMPTS, ByteBuffer.allocate(4).putInt(5).array(),
                        RECEIVED_TOPIC, TOPIC,
                        RECEIVED_PARTITION, 0,
                        OFFSET, 0L));
        when(joinPoint.getArgs()).thenReturn(new Object[]{filingReceivedMessage});
        when(filingReceivedMessage.getPayload()).thenReturn(filingReceived);
        when(filingReceivedMessage.getHeaders()).thenReturn(headers);
        when(filingReceived.getSubmission()).thenReturn(filingReceivedSubmission);
        when(filingReceivedSubmission.getTransactionId()).thenReturn(TRANSACTION_ID);
        when(joinPoint.proceed()).thenThrow(RetryableException.class);

        // when
        Executable actual = () -> aspect.manageStructuredLogging(joinPoint);

        //then
        assertThrows(RetryableException.class, actual);
        assertTrue(ERROR_EVENT_PATTERN.matcher(capture.getOut()).find());
        assertTrue(MAX_RETRY_ATTEMPTS_REACHED_PATTERN.matcher(capture.getOut()).find());
        assertTrue(REQUEST_ID_INITIALISED_PATTERN.matcher(capture.getOut()).find());
        assertTrue(RETRY_COUNT_FOUR_PATTERN.matcher(capture.getOut()).find());
        assertTrue(MAIN_TOPIC_PATTERN.matcher(capture.getOut()).find());
        assertTrue(PARTITION_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(OFFSET_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(TRANSACTION_ID_PATTERN.matcher(capture.getOut()).find());
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldLogInfoWhenInvalidPayload(CapturedOutput capture) {
        // given
        MessageHeaders headers = new MessageHeaders(
                Map.of(
                        RECEIVED_TOPIC, TOPIC,
                        RECEIVED_PARTITION, 0,
                        OFFSET, 0L));
        when(joinPoint.getArgs()).thenReturn(new Object[]{invalidMessage});
        when(invalidMessage.getPayload()).thenReturn("message payload");
        when(invalidMessage.getHeaders()).thenReturn(headers);

        // when
        Executable actual = () -> aspect.manageStructuredLogging(joinPoint);

        //then
        assertThrows(NonRetryableException.class, actual);
        assertTrue(ERROR_EVENT_PATTERN.matcher(capture.getOut()).find());
        assertTrue(INVALID_PAYLOAD_PATTERN.matcher(capture.getOut()).find());
        assertTrue(REQUEST_ID_INITIALISED_PATTERN.matcher(capture.getOut()).find());
        assertTrue(RETRY_COUNT_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(MAIN_TOPIC_PATTERN.matcher(capture.getOut()).find());
        assertTrue(PARTITION_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(OFFSET_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertFalse(capture.getOut().contains("transaction_id"));
    }

    private static void verifyInfoLogMap(CapturedOutput capture) {
        assertTrue(INFO_EVENT_PATTERN.matcher(capture.getOut()).find());
        assertTrue(REQUEST_ID_INITIALISED_PATTERN.matcher(capture.getOut()).find());
        assertTrue(RETRY_COUNT_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(MAIN_TOPIC_PATTERN.matcher(capture.getOut()).find());
        assertTrue(PARTITION_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(OFFSET_ZERO_PATTERN.matcher(capture.getOut()).find());
        assertTrue(TRANSACTION_ID_PATTERN.matcher(capture.getOut()).find());
    }
}