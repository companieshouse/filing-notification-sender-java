package uk.gov.companieshouse.filing.common.client;

import static uk.gov.companieshouse.filing.Application.NAMESPACE;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.common.exception.RetryableException;
import uk.gov.companieshouse.filing.common.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class TransactionsApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final String TRANSACTIONS_URI = "/private/transactions/%s";

    private final InternalApiClientFactory internalApiClientFactory;

    public TransactionsApiClient(InternalApiClientFactory internalApiClientFactory) {
        this.internalApiClientFactory = internalApiClientFactory;
    }

    public Transaction getTransaction(String transactionId) {
        String resourceUri = TRANSACTIONS_URI.formatted(transactionId);
        LOGGER.info("Calling GET %s".formatted(resourceUri), DataMapHolder.getLogMap());

        InternalApiClient client = internalApiClientFactory.getPrivateApiHostClient();
        try {
            return client.privateTransaction()
                    .get(resourceUri)
                    .execute()
                    .getData();
        } catch (ApiErrorResponseException ex) {
            final int statusCode = ex.getStatusCode();
            String errorMsg = "GET %s failed, status code: [%d]".formatted(resourceUri, statusCode);
            LOGGER.error(errorMsg, ex, DataMapHolder.getLogMap());

            if (HttpStatus.BAD_REQUEST.value() == statusCode || HttpStatus.CONFLICT.value() == statusCode) {
                throw new NonRetryableException(errorMsg, ex);
            } else {
                throw new RetryableException(errorMsg, ex);
            }
        } catch (URIValidationException ex) {
            String errorMsg = "GET %s failed due to invalid URI".formatted(resourceUri);
            LOGGER.error(errorMsg, ex, DataMapHolder.getLogMap());
            throw new NonRetryableException(errorMsg, ex);
        }
    }
}
