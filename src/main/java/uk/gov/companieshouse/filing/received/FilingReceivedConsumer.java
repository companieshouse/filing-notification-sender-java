package uk.gov.companieshouse.filing.received;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.Consumer;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.exception.RetryableException;
import uk.gov.companieshouse.filing.common.kafka.MessageFlags;

@Component
public class FilingReceivedConsumer implements Consumer<FilingReceived> {

    private final Service<FilingReceived> service;
    private final MessageFlags messageFlags;

    public FilingReceivedConsumer(Service<FilingReceived> service, MessageFlags messageFlags) {
        this.service = service;
        this.messageFlags = messageFlags;
    }

    @KafkaListener(
            id = "${kafka.consumer.topic.filing-received}-consumer",
            containerFactory = "receivedListenerContainerFactory",
            topics = "${kafka.consumer.topic.filing-received}",
            groupId = "${kafka.consumer.group}"
    )
    @Override
    public void consume(Message<FilingReceived> message) {
        try {
            service.handlePayload(message.getPayload());
        } catch (RetryableException ex) {
            messageFlags.setRetryable(true);
            throw ex;
        }
    }
}
