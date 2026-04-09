package uk.gov.companieshouse.filing.processed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.Mapper;

@Component
public class FilingProcessedMapper implements Mapper<FilingProcessed> {

    @Override
    public MessageSend map(FilingProcessed payload, Transaction transaction) {
        return new MessageSend();
    }
}
