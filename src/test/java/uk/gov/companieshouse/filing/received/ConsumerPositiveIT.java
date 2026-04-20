package uk.gov.companieshouse.filing.received;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ConsumerPositiveIT extends AbstractFilingReceivedConsumerIT {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void shouldConsumeFilingReceivedMessageAndProcessSuccessfully() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingReceived(), FilingReceived.class);

        stubTransactionsApiResponse(200);
        stubKafkaApiResponse(200);

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 0);
        verifyTransactionsApiRequest(1);
        verifyKafkaApiRequest(2, "");
    }
}
