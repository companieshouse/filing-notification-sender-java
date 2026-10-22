package uk.gov.companieshouse.filing.processed.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.mapper.RandomNumberGenerator;
import uk.gov.companieshouse.filing.common.testdata.TransactionTestDataBuilder;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingProcessedMapperTest {

    @InjectMocks
    private FilingProcessedMapper filingProcessedMapper;
    @Mock
    private FilingProcessedTemplateMapper filingProcessedTemplateMapper;
    @Mock
    private RandomNumberGenerator randomNumberGenerator;
    @Mock
    private FilingProcessedDataMapper filingProcessedDataMapper;

    @Test
    void shouldMapAcceptedFilingProcessedAndTransactionToMessageSend() {
        // given
        FilingProcessed payload = buildPayload("accepted");
        Transaction transaction = TransactionTestDataBuilder.aTransaction().withFilingDescription("submissionId", "originalDesc").build();

        when(filingProcessedTemplateMapper.mapDescriptionTemplates(any(), any()))
                .thenReturn(new DescriptionTemplate("mappedDesc", "acceptTemp", "rejectTemp"));
        when(randomNumberGenerator.random()).thenReturn("12345");
        when(filingProcessedDataMapper.map(any(), any(), any(), any())).thenReturn(new MessageSendData());

        MessageSend expected = buildMessageSend("acceptTemp");

        // when
        MessageSend actual = filingProcessedMapper.map(payload, transaction);

        // then
        assertEquals(expected, actual);
        verify(filingProcessedTemplateMapper).mapDescriptionTemplates("originalDesc", transaction);
        verify(randomNumberGenerator).random();
        verify(filingProcessedDataMapper).map(transaction, "originalDesc", payload, "mappedDesc");
    }

    @Test
    void shouldMapRejectedFilingProcessedAndTransactionToMessageSend() {
        // given
        FilingProcessed payload = buildPayload("rejected");
        Transaction transaction = TransactionTestDataBuilder.aTransaction().withFilingDescription("submissionId", "originalDesc").build();

        when(filingProcessedTemplateMapper.mapDescriptionTemplates(any(), any()))
                .thenReturn(new DescriptionTemplate("mappedDesc", "acceptTemp", "rejectTemp"));
        when(randomNumberGenerator.random()).thenReturn("12345");
        when(filingProcessedDataMapper.map(any(), any(), any(), any())).thenReturn(new MessageSendData());

        MessageSend expected = buildMessageSend("rejectTemp");

        // when
        MessageSend actual = filingProcessedMapper.map(payload, transaction);

        // then
        assertEquals(expected, actual);
        verify(filingProcessedTemplateMapper).mapDescriptionTemplates("originalDesc", transaction);
        verify(randomNumberGenerator).random();
        verify(filingProcessedDataMapper).map(transaction, "originalDesc", payload, "mappedDesc");
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
}