package uk.gov.companieshouse.filing.processed;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ConsumerNonRetryableExceptionIT extends AbstractFilingProcessedConsumerIT {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void testRepublishToFilingProcessedInvalidMessageTopicWhenTransactionsApiNonRetryableErrorResponse() throws Exception {
        // given
        byte[] message = buildFilingProcessedAcceptedBytes();

        stubTransactionsApiResponse(400);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(0);
    }

    @Test
    void testRepublishToFilingProcessedInvalidMessageTopicWhenKafkaApiNonRetryableErrorResponse() throws Exception {
        // given
        byte[] message = buildFilingProcessedAcceptedBytes();

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(400);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(1);
    }

    @Test
    void testPublishToFilingProcessedInvalidMessageTopicIfInvalidDataDeserialised() {
        // given
        byte[] message = writePayloadToBytes("bad data", String.class);

        // when
        publish(message);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verifyTransactionsApiRequest(0);
        verifyKafkaApiRequest(0);
    }
}
