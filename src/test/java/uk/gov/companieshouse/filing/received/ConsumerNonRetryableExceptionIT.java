package uk.gov.companieshouse.filing.received;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ConsumerNonRetryableExceptionIT extends AbstractFilingReceivedConsumerIT {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void testRepublishToFilingReceivedInvalidMessageTopicWhenTransactionsApiNonRetryableErrorResponse() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingReceived(), FilingReceived.class);

        stubTransactionsApiResponse(400);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(0);
    }

    @Test
    void testRepublishToFilingReceivedInvalidMessageTopicWhenKafkaApiNonRetryableErrorResponse() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingReceived(), FilingReceived.class);

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(400);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(1); // Check whether the item is skipped or if the whole message should be retried
    }

    @Test
    void testPublishToFilingReceivedInvalidMessageTopicIfInvalidDataDeserialised() {
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
