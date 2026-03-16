package uk.gov.companieshouse.filing.processed;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.exception.RetryableException;

@SpringBootTest
class ConsumerRetryableExceptionIT extends AbstractFilingProcessedConsumerIT {

    @MockitoBean
    private Service<FilingProcessed> service;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 5);
    }

    @Test
    void testRepublishToErrorTopicThroughRetryTopics() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingProcessed(), FilingProcessed.class);

        doThrow(RetryableException.class).when(service).handlePayload(any());

        // when
        testProducer.send(new ProducerRecord<>(MAIN_TOPIC, 0, System.currentTimeMillis(), "key", message));
        if (!testConsumerAspect.getLatch().await(30, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        // then
        ConsumerRecords<?, ?> records = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(10000L), 6);
        assertThat(recordsPerTopic(records, MAIN_TOPIC)).isOne();
        assertThat(recordsPerTopic(records, RETRY_TOPIC)).isEqualTo(4);
        assertThat(recordsPerTopic(records, ERROR_TOPIC)).isOne();
        assertThat(recordsPerTopic(records, INVALID_TOPIC)).isZero();
        verify(0, anyRequestedFor(anyUrl()));
    }
}
