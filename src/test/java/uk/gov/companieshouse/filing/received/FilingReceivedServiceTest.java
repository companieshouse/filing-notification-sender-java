package uk.gov.companieshouse.filing.received;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.filing.common.client.KafkaApiClient;
import uk.gov.companieshouse.filing.common.client.TransactionsApiClient;
import uk.gov.companieshouse.filing.received.mapper.FilingReceivedMapper;

@ExtendWith(MockitoExtension.class)
class FilingReceivedServiceTest {

    @InjectMocks
    private FilingReceivedService filingReceivedService;
    @Mock
    private TransactionsApiClient transactionsApiClient;
    @Mock
    private FilingReceivedMapper filingReceivedMapper;
    @Mock
    private KafkaApiClient kafkaApiClient;

    @Mock
    private FilingReceived filingReceived;
    @Mock
    private SubmissionRecord submissionRecord;
    @Mock
    private uk.gov.companieshouse.api.model.transaction.Transaction transaction;
    @Mock
    private uk.gov.companieshouse.filing.received.Transaction item1;
    @Mock
    private uk.gov.companieshouse.filing.received.Transaction item2;
    @Mock
    private MessageSend messageSend1;
    @Mock
    private MessageSend messageSend2;

    @Test
    void shouldHandlePayloadSuccessfully() {
        // given
        when(filingReceived.getSubmission()).thenReturn(submissionRecord);
        when(submissionRecord.getTransactionId()).thenReturn("transaction-id");
        when(transactionsApiClient.getTransaction(any())).thenReturn(transaction);
        when(filingReceived.getItems()).thenReturn(List.of(item1, item2));
        when(filingReceivedMapper.map(any(), eq(item1), any())).thenReturn(messageSend1);
        when(filingReceivedMapper.map(any(), eq(item2), any())).thenReturn(messageSend2);

        // when
        filingReceivedService.handlePayload(filingReceived);

        // then
        verify(transactionsApiClient).getTransaction("transaction-id");
        verify(filingReceivedMapper).map(filingReceived, item1, transaction);
        verify(filingReceivedMapper).map(filingReceived, item2, transaction);
        verify(kafkaApiClient).postMessageSend(messageSend1);
        verify(kafkaApiClient).postMessageSend(messageSend2);
    }
}