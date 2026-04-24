package uk.gov.companieshouse.filing.processed;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ConsumerRetryableExceptionIT extends AbstractFilingProcessedConsumerIT {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 5);
    }

    @Test
    void testRepublishToErrorTopicThroughRetryTopicsWhenTransactionsApiErrorResponse() throws Exception {
        // given
        byte[] message = buildFilingProcessedAcceptedBytes();

        stubTransactionsApiResponse(500);

        // when
        publishAndAwaitConsumerLatch(message, 30);

        // then
        assertExpectedRecordsPerTopic(4, 1, 0);
        verifyTransactionsApiRequest(5);
        verifyKafkaApiRequest(0);
    }

    @Test
    void testRepublishToErrorTopicThroughRetryTopicsWhenKafkaApiErrorResponse() throws Exception {
        // given
        byte[] message = buildFilingProcessedAcceptedBytes();

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(500);

        // when
        publishAndAwaitConsumerLatch(message, 30);

        // then
        assertExpectedRecordsPerTopic(4, 1, 0);
        verifyTransactionsApiRequest(5);
        verifyKafkaApiRequest(5);
    }
}
