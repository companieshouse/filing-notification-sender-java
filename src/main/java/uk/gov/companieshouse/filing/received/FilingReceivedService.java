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
        payload.getItems().forEach(item -> {
            MessageSend messageSend = filingReceivedMapper.map(payload, item, transaction);
            kafkaApiClient.postMessageSend(messageSend);
        });
    }
}
