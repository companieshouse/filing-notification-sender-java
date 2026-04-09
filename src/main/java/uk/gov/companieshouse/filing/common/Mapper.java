package uk.gov.companieshouse.filing.common;

import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface Mapper<T> {

    MessageSend map(T payload, Transaction transaction);
}
