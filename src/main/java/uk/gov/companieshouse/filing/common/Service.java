package uk.gov.companieshouse.filing.common;

public interface Service<T> {

    void handlePayload(T payload);
}
