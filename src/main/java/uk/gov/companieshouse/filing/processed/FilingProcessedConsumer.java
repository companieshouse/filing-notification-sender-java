package uk.gov.companieshouse.filing.processed;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.Consumer;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.exception.RetryableException;
import uk.gov.companieshouse.filing.common.kafka.MessageFlags;

@Component
public class FilingProcessedConsumer implements Consumer<FilingProcessed> {

    private final Service<FilingProcessed> service;
    private final MessageFlags messageFlags;

    public FilingProcessedConsumer(Service<FilingProcessed> service, MessageFlags messageFlags) {
        this.service = service;
        this.messageFlags = messageFlags;
    }

    @KafkaListener(
            id = "${kafka.consumer.topic.filing-processed}-consumer",
            containerFactory = "processedListenerContainerFactory",
            topics = "${kafka.consumer.topic.filing-processed}",
            groupId = "${kafka.consumer.group}"
    )
    @Override
    public void consume(Message<FilingProcessed> message) {
        try {
            service.handlePayload(message.getPayload());
        } catch (RetryableException ex) {
            messageFlags.setRetryable(true);
            throw ex;
        }
    }
}
