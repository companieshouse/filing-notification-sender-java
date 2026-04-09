package uk.gov.companieshouse.filing.processed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class FilingProcessedMapper {

    public MessageSend map(FilingProcessed payload, Transaction transaction) {
        return new MessageSend();
    }
}
