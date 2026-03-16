package uk.gov.companieshouse.filing.common.kafka;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.List;
import org.apache.avro.io.DatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class KafkaPayloadSerialiserTest {

    @Mock
    private DatumWriter<FilingReceived> writer;

    @Test
    void testSerialiseFilingReceived() {
        // given
        try (KafkaPayloadSerialiser<FilingReceived> serialiser = new KafkaPayloadSerialiser<>(FilingReceived.class)) {
            FilingReceived filingReceived = new FilingReceived();
            filingReceived.setApplicationId("applicationId");
            filingReceived.setChannelId("channelId");
            PresenterRecord presenter = new PresenterRecord("forename", "language", "surname", "userId");
            filingReceived.setPresenter(presenter);
            filingReceived.setItems(List.of());
            SubmissionRecord submission = new SubmissionRecord("companyNumber", "companyName", "receivedAt", "transactionId");
            filingReceived.setSubmission(submission);

            // when
            byte[] actual = serialiser.serialize("topic", filingReceived);

            // then
            assertTrue(actual.length > 0);
        }
    }

    @Test
    void testSerialiseThrowsNonRetryableExceptionWhenIOException() throws IOException {
        // given
        KafkaPayloadSerialiser<FilingReceived> serialiser = spy(new KafkaPayloadSerialiser<>(FilingReceived.class));
        doReturn(writer).when(serialiser).getDatumWriter();
        doThrow(IOException.class).when(writer).write(any(), any());

        // when
        Executable actual = () -> serialiser.serialize("topic", new FilingReceived());

        // then
        NonRetryableException exception = assertThrows(NonRetryableException.class, actual);
        assertInstanceOf(IOException.class, exception.getCause());
    }
}
