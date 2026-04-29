package uk.gov.companieshouse.filing.processed;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ConsumerPositiveIT extends AbstractFilingProcessedConsumerIT {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void shouldConsumeFilingProcessedMessageAcceptedAndProcessSuccessfully() throws Exception {
        // given
        byte[] message = buildFilingProcessedAcceptedBytes();

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(200);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 0);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(MESSAGE_SEND_ACCEPTED_PATH);
    }

    @Test
    void shouldConsumeFilingProcessedMessageRejectedAndProcessSuccessfully() throws Exception {
        // given
        byte[] message = buildFilingProcessedRejectedBytes();

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(200);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 0);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(MESSAGE_SEND_REJECTED_PATH);
    }
}
