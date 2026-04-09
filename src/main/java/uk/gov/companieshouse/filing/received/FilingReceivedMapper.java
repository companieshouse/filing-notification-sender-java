package uk.gov.companieshouse.filing.received;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;

@Component
public class FilingReceivedMapper {

    public MessageSend map(FilingReceived payload, uk.gov.companieshouse.filing.received.Transaction item,
            uk.gov.companieshouse.api.model.transaction.Transaction transaction) {
        return new MessageSend();
    }
}
