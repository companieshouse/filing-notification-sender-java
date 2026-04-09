package uk.gov.companieshouse.filing.received;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;

@SpringBootTest
class ConsumerNonRetryableExceptionIT extends AbstractFilingReceivedConsumerIT {

    @MockitoBean
    private Service<FilingReceived> service;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void testRepublishToFilingReceivedInvalidMessageTopicIfNonRetryableExceptionThrown() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingReceived(), FilingReceived.class);

        doThrow(NonRetryableException.class).when(service).handlePayload(any());

        // when
        publishAndAwaitConsumerLatch(message, 10);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verify(0, anyRequestedFor(anyUrl()));
    }

    @Test
    void testPublishToFilingReceivedInvalidMessageTopicIfInvalidDataDeserialised() {
        // given
        byte[] message = writePayloadToBytes("bad data", String.class);

        // when
        publish(message);

        // then
        assertExpectedRecordsPerTopic(0, 0, 1);
        verify(0, anyRequestedFor(anyUrl()));
    }
}
