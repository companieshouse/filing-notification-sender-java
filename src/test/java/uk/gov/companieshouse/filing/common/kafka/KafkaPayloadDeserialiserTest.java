package uk.gov.companieshouse.filing.common.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.gov.companieshouse.filing.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

class KafkaPayloadDeserialiserTest {

    @Test
    void testDeserialiseFilingReceived() {
        // given
        try (KafkaPayloadDeserialiser<FilingReceived> deserialiser = new KafkaPayloadDeserialiser<>(FilingReceived.class)) {
            FilingReceived filingReceived = new FilingReceived();
            filingReceived.setApplicationId("applicationId");
            filingReceived.setChannelId("channelId");
            PresenterRecord presenter = new PresenterRecord("forename", "language", "surname", "userId");
            filingReceived.setPresenter(presenter);
            filingReceived.setItems(List.of());
            SubmissionRecord submission = new SubmissionRecord("companyNumber", "companyName", "receivedAt", "transactionId");
            filingReceived.setSubmission(submission);

            // when
            FilingReceived actual = deserialiser.deserialize("topic", writePayloadToBytes(filingReceived, FilingReceived.class));

            // then
            assertEquals(filingReceived, actual);
        }
    }

    @Test
    void testDeserialiseThrowsInvalidPayloadExceptionWhenIOException() {
        // given
        try (KafkaPayloadDeserialiser<FilingReceived> deserialiser = new KafkaPayloadDeserialiser<>(FilingReceived.class)) {

            // when
            Executable actual = () -> deserialiser.deserialize("topic", writePayloadToBytes("hello", String.class));

            // then
            InvalidPayloadException exception = assertThrows(InvalidPayloadException.class, actual);
            assertInstanceOf(IOException.class, exception.getCause());
        }
    }

    @Test
    void testDeserialiseThrowsInvalidPayloadExceptionWhenAvroRuntimeException() {
        // given
        try (KafkaPayloadDeserialiser<FilingReceived> deserialiser = new KafkaPayloadDeserialiser<>(FilingReceived.class)) {

            // when
            Executable actual = () -> deserialiser.deserialize("topic", "invalid".getBytes(StandardCharsets.UTF_8));

            // then
            InvalidPayloadException exception = assertThrows(InvalidPayloadException.class, actual);
            assertInstanceOf(AvroRuntimeException.class, exception.getCause());
        }
    }

    private static <T> byte[] writePayloadToBytes(T data, Class<T> type) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
            DatumWriter<T> writer = new ReflectDatumWriter<>(type);
            writer.write(data, encoder);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}