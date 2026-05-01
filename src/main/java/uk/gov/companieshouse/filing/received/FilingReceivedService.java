package uk.gov.companieshouse.filing.received;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.client.KafkaApiClient;
import uk.gov.companieshouse.filing.common.client.TransactionsApiClient;
import uk.gov.companieshouse.filing.received.mapper.FilingReceivedMapper;

@Component
public class FilingReceivedService implements Service<FilingReceived> {

    private final TransactionsApiClient transactionsApiClient;
    private final FilingReceivedMapper filingReceivedMapper;
    private final KafkaApiClient kafkaApiClient;

    public FilingReceivedService(TransactionsApiClient transactionsApiClient, FilingReceivedMapper filingReceivedMapper,
            KafkaApiClient kafkaApiClient) {
        this.transactionsApiClient = transactionsApiClient;
        this.filingReceivedMapper = filingReceivedMapper;
        this.kafkaApiClient = kafkaApiClient;
    }

    @Override
    public void handlePayload(FilingReceived payload) {
        Transaction transaction = transactionsApiClient.getTransaction(payload.getSubmission().getTransactionId());
        // As of April 2026, there are no front end flows to submit multiple filings per transactions. If made possible, it
        // introduces risk of duplicate emails e.g. when the first POST succeeds but the second POST fails, the whole message
        // would be retried.
        // Once possible, emails should be changed to go through gov notify - where duplicate emails can be tracked by ID.
        // See KAF-432 for more details
        payload.getItems().forEach(item -> {
            MessageSend messageSend = filingReceivedMapper.map(payload, item, transaction);
            kafkaApiClient.postMessageSend(messageSend);
        });
    }
}
