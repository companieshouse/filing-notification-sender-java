package uk.gov.companieshouse.filing.processed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.Service;
import uk.gov.companieshouse.filing.common.client.KafkaApiClient;
import uk.gov.companieshouse.filing.common.client.TransactionsApiClient;
import uk.gov.companieshouse.filing.processed.mapper.FilingProcessedMapper;

@Component
public class FilingProcessedService implements Service<FilingProcessed> {

    private final TransactionsApiClient transactionsApiClient;
    private final FilingProcessedMapper filingProcessedMapper;
    private final KafkaApiClient kafkaApiClient;

    public FilingProcessedService(TransactionsApiClient transactionsApiClient, FilingProcessedMapper filingProcessedMapper,
            KafkaApiClient kafkaApiClient) {
        this.transactionsApiClient = transactionsApiClient;
        this.filingProcessedMapper = filingProcessedMapper;
        this.kafkaApiClient = kafkaApiClient;
    }

    @Override
    public void handlePayload(FilingProcessed payload) {
        Transaction transaction = transactionsApiClient.getTransaction(payload.getSubmission().getTransactionId());
        MessageSend messageSend = filingProcessedMapper.map(payload, transaction);
        kafkaApiClient.postMessageSend(messageSend);
    }
}
