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
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;

@SpringBootTest
class ConsumerNonRetryableExceptionIT extends AbstractFilingProcessedConsumerIT {

    @MockitoBean
    private Service<FilingProcessed> service;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("steps", () -> 1);
    }

    @Test
    void testRepublishToFilingProcessedInvalidMessageTopicIfNonRetryableExceptionThrown() throws Exception {
        // given
        byte[] message = writePayloadToBytes(buildFilingProcessed(), FilingProcessed.class);

        doThrow(NonRetryableException.class).when(service).handlePayload(any());

        // when
        testProducer.send(new ProducerRecord<>(MAIN_TOPIC, 0, System.currentTimeMillis(), "key", message));
        if (!testConsumerAspect.getLatch().await(60L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        // then
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(10000L), 2);
        assertThat(recordsPerTopic(consumerRecords, MAIN_TOPIC)).isOne();
        assertThat(recordsPerTopic(consumerRecords, RETRY_TOPIC)).isZero();
        assertThat(recordsPerTopic(consumerRecords, ERROR_TOPIC)).isZero();
        assertThat(recordsPerTopic(consumerRecords, INVALID_TOPIC)).isOne();
        verify(0, anyRequestedFor(anyUrl()));
    }

    @Test
    void testPublishToFilingProcessedInvalidMessageTopicIfInvalidDataDeserialised() {
        // given
        byte[] message = writePayloadToBytes("bad data", String.class);

        // when
        testProducer.send(new ProducerRecord<>(MAIN_TOPIC, 0, System.currentTimeMillis(), "key", message));

        // then
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(10000L), 2);
        assertThat(recordsPerTopic(consumerRecords, MAIN_TOPIC)).isOne();
        assertThat(recordsPerTopic(consumerRecords, RETRY_TOPIC)).isZero();
        assertThat(recordsPerTopic(consumerRecords, ERROR_TOPIC)).isZero();
        assertThat(recordsPerTopic(consumerRecords, INVALID_TOPIC)).isOne();
        verify(0, anyRequestedFor(anyUrl()));
    }
}
