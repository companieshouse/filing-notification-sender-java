package uk.gov.companieshouse.filing.common;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.Iterables;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@WireMockTest(httpPort = 8889)
@Import(TestKafkaConfig.class)
public abstract class AbstractConsumerIT {

    // Container reuse dramatically speeds up test execution
    // Warnings like "you must set 'testcontainers.reuse.enable=true'" are false positives
    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:latest")
            .withReuse(true);
    private static final String GROUP = "filing-notification-sender";

    @MockitoBean
    private RandomNumberGenerator randomNumberGenerator;
    @Autowired
    private KafkaConsumer<String, byte[]> testConsumer;
    @Autowired
    private KafkaProducer<String, byte[]> testProducer;
    @Autowired
    private TestConsumerAspect testConsumerAspect;

    private final String mainTopic;
    private final String retryTopic;
    private final String errorTopic;
    private final String invalidTopic;

    protected AbstractConsumerIT(String mainTopic) {
        this.mainTopic = mainTopic;
        this.retryTopic = "%s-%s-retry".formatted(mainTopic, GROUP);
        this.errorTopic = "%s-%s-error".formatted(mainTopic, GROUP);
        this.invalidTopic = "%s-%s-invalid".formatted(mainTopic, GROUP);
    }

    @BeforeAll
    static void beforeAll() {
        kafka.start();
    }

    @BeforeEach
    protected void setup(@Autowired KafkaListenerEndpointRegistry registry) {
        registry.getAllListenerContainers() // Ensure all listener containers are assigned to partitions before tests run
                .forEach(container -> ContainerTestUtils.waitForAssignment(container, 1));
        testConsumerAspect.resetLatch();
        testConsumer.subscribe(List.of(mainTopic, retryTopic, errorTopic, invalidTopic));
        testConsumer.poll(Duration.ofMillis(1000));
        WireMock.reset();

        when(randomNumberGenerator.random()).thenReturn("12345");
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    protected static <T> T readAvroJson(String filename, Class<T> type, Schema schema) throws IOException {
        String json = IOUtils.resourceToString(filename, StandardCharsets.UTF_8);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
        DatumReader<T> reader = new ReflectDatumReader<>(type);
        return reader.read(null, decoder);
    }

    protected static <T> byte[] writePayloadToBytes(T data, Class<T> type) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
            DatumWriter<T> writer = new ReflectDatumWriter<>(type);
            writer.write(data, encoder);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static void stubTransactionsApiResponse(int statusCode) throws IOException {
        if (statusCode != 200) {
            stubFor(get("/private/transactions/021787-298317-763347")
                    .willReturn(aResponse()
                            .withStatus(statusCode)));
        } else {
            String response = IOUtils.resourceToString("/processed/transaction-response.json", StandardCharsets.UTF_8);
            stubFor(get("/private/transactions/021787-298317-763347")
                    .willReturn(aResponse()
                            .withStatus(statusCode)
                            .withBody(response)));
        }
    }

    protected static void stubKafkaApiResponse(int statusCode) {
        stubFor(post("/message-send")
                .willReturn(aResponse()
                        .withStatus(statusCode)));
    }

    protected void publish(byte[] message) {
        testProducer.send(new ProducerRecord<>(mainTopic, 0, System.currentTimeMillis(), "key", message));
    }

    protected void publishAndAwaitConsumerLatch(byte[] message, int latchTimeout) throws InterruptedException {
        publish(message);
        if (!testConsumerAspect.getLatch().await(latchTimeout, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }
    }

    protected void assertExpectedRecordsPerTopic(int retrySize, int errorSize, int invalidSize) {
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(1000L), 7);
        assertEquals(1, Iterables.size(consumerRecords.records(mainTopic)));
        assertEquals(retrySize, Iterables.size(consumerRecords.records(retryTopic)));
        assertEquals(errorSize, Iterables.size(consumerRecords.records(errorTopic)));
        assertEquals(invalidSize, Iterables.size(consumerRecords.records(invalidTopic)));
    }

    protected static void verifyTransactionsApiRequest(int count) {
        verify(count, getRequestedFor(urlEqualTo("/private/transactions/021787-298317-763347")));
    }

    protected static void verifyKafkaApiRequest(int count) {
        verify(count, postRequestedFor(urlEqualTo("/message-send")));
    }

    protected static void verifyKafkaApiRequest(String requestBodyFilename) throws IOException {
        String requestBody = IOUtils.resourceToString(requestBodyFilename, StandardCharsets.UTF_8);
        verify(1, postRequestedFor(urlEqualTo("/message-send"))
                .withRequestBody(equalToJson(requestBody, false, true)));
    }
}