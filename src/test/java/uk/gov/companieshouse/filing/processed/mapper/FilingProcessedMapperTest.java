package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Filing;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.RandomNumberGenerator;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class FilingProcessedMapperTest {

    @InjectMocks
    private FilingProcessedMapper filingProcessedMapper;
    @Mock
    private DescriptionTemplateMapper descriptionTemplateMapper;
    @Mock
    private RandomNumberGenerator randomNumberGenerator;
    @Mock
    private MessageSendDataMapper messageSendDataMapper;

    @Test
    void shouldMapAcceptedFilingProcessedAndTransactionToMessageSend() {
        // given
        FilingProcessed payload = buildPayload("accepted");
        Transaction transaction = buildTransaction();

        when(descriptionTemplateMapper.mapDescriptionTemplate(any()))
                .thenReturn(new DescriptionTemplate("mappedDesc", "acceptTemp", "rejectTemp"));
        when(randomNumberGenerator.fiveDigitNumber()).thenReturn("12345");
        when(messageSendDataMapper.map(any(), any(), any(), any())).thenReturn(new MessageSendData());

        MessageSend expected = buildMessageSend("acceptTemp");

        // when
        MessageSend actual = filingProcessedMapper.map(payload, transaction);

        // then
        assertEquals(expected, actual);
        verify(descriptionTemplateMapper).mapDescriptionTemplate("originalDesc");
        verify(randomNumberGenerator).fiveDigitNumber();
        verify(messageSendDataMapper).map(transaction, "originalDesc", payload, "mappedDesc");
    }

    @Test
    void shouldMapRejectedFilingProcessedAndTransactionToMessageSend() {
        // given
        FilingProcessed payload = buildPayload("rejected");
        Transaction transaction = buildTransaction();

        when(descriptionTemplateMapper.mapDescriptionTemplate(any()))
                .thenReturn(new DescriptionTemplate("mappedDesc", "acceptTemp", "rejectTemp"));
        when(randomNumberGenerator.fiveDigitNumber()).thenReturn("12345");
        when(messageSendDataMapper.map(any(), any(), any(), any())).thenReturn(new MessageSendData());

        MessageSend expected = buildMessageSend("rejectTemp");

        // when
        MessageSend actual = filingProcessedMapper.map(payload, transaction);

        // then
        assertEquals(expected, actual);
        verify(descriptionTemplateMapper).mapDescriptionTemplate("originalDesc");
        verify(randomNumberGenerator).fiveDigitNumber();
        verify(messageSendDataMapper).map(transaction, "originalDesc", payload, "mappedDesc");
    }

    private static MessageSend buildMessageSend(String statusType) {
        return new MessageSend()
                .appId("filing_processed_notification_sender." + statusType)
                .messageType(statusType)
                .messageId("<transactionId.12345@companieshouse.gov.uk>")
                .data(new MessageSendData())
                .createdAt("dateOfCreation")
                .userId("userId");
    }

    private static FilingProcessed buildPayload(String status) {
        ResponseRecord responseRecord = new ResponseRecord();
        responseRecord.setSubmissionId("submissionId");
        responseRecord.setStatus(status);
        responseRecord.setDateOfCreation("dateOfCreation");

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("transactionId");

        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setUserId("userId");

        FilingProcessed payload = new FilingProcessed();
        payload.setResponse(responseRecord);
        payload.setSubmission(submissionRecord);
        payload.setPresenter(presenterRecord);

        return payload;
    }

    private static Transaction buildTransaction() {
        Filing filing = new Filing();
        filing.setDescription("originalDesc");

        Transaction transaction = new Transaction();
        transaction.setFilings(Map.of("submissionId", filing));

        return transaction;
    }
}