package uk.gov.companieshouse.filing.common;

import org.springframework.messaging.Message;

public interface Consumer<T> {

    void consume(Message<T> message);
}
