package uk.gov.companieshouse.filing.common.client;

import static uk.gov.companieshouse.filing.Application.NAMESPACE;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.common.exception.RetryableException;
import uk.gov.companieshouse.filing.common.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class KafkaApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final String MESSAGE_SEND_URI = "/message-send";

    private final InternalApiClientFactory internalApiClientFactory;

    public KafkaApiClient(InternalApiClientFactory internalApiClientFactory) {
        this.internalApiClientFactory = internalApiClientFactory;
    }

    public void postMessageSend(MessageSend messageSend) {
        LOGGER.info("Calling POST %s".formatted(MESSAGE_SEND_URI), DataMapHolder.getLogMap());

        InternalApiClient client = internalApiClientFactory.getKafkaApiHostClient();
        try {
            client.messageSendHandler()
                    .postMessageSend(MESSAGE_SEND_URI, messageSend)
                    .execute();
        } catch (ApiErrorResponseException ex) {
            final int statusCode = ex.getStatusCode();
            String errorMsg = "POST %s failed, status code: [%d]".formatted(MESSAGE_SEND_URI, statusCode);
            LOGGER.error(errorMsg, ex, DataMapHolder.getLogMap());

            if (HttpStatus.BAD_REQUEST.value() == statusCode || HttpStatus.CONFLICT.value() == statusCode) {
                throw new NonRetryableException(errorMsg, ex);
            } else {
                throw new RetryableException(errorMsg, ex);
            }
        }
    }
}
