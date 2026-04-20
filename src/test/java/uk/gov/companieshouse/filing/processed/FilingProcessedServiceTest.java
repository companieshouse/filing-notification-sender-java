package uk.gov.companieshouse.filing.processed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.client.KafkaApiClient;
import uk.gov.companieshouse.filing.common.client.TransactionsApiClient;
import uk.gov.companieshouse.filing.processed.mapper.FilingProcessedMapper;

@ExtendWith(MockitoExtension.class)
class FilingProcessedServiceTest {

    @InjectMocks
    private FilingProcessedService filingProcessedService;
    @Mock
    private TransactionsApiClient transactionsApiClient;
    @Mock
    private FilingProcessedMapper filingProcessedMapper;
    @Mock
    private KafkaApiClient kafkaApiClient;

    @Mock
    private FilingProcessed filingProcessed;
    @Mock
    private SubmissionRecord submissionRecord;
    @Mock
    private Transaction transaction;
    @Mock
    private MessageSend messageSend;

    @Test
    void shouldHandlePayloadSuccessfully() {
        // given
        when(filingProcessed.getSubmission()).thenReturn(submissionRecord);
        when(submissionRecord.getTransactionId()).thenReturn("transaction-id");
        when(transactionsApiClient.getTransaction(any())).thenReturn(transaction);
        when(filingProcessedMapper.map(any(), any())).thenReturn(messageSend);

        // when
        filingProcessedService.handlePayload(filingProcessed);

        // then
        verify(transactionsApiClient).getTransaction("transaction-id");
        verify(filingProcessedMapper).map(filingProcessed, transaction);
        verify(kafkaApiClient).postMessageSend(messageSend);
    }
}