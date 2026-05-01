package uk.gov.companieshouse.filing.received.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import uk.gov.companieshouse.filing.common.mapper.RandomNumberGenerator;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class FilingReceivedMapperTest {

    @InjectMocks
    private FilingReceivedMapper filingReceivedMapper;
    @Mock
    private FilingReceivedTemplateMapper filingReceivedTemplateMapper;
    @Mock
    private RandomNumberGenerator randomNumberGenerator;
    @Mock
    private FilingReceivedDataMapper filingReceivedDataMapper;
    @Mock
    private LocalDateTimeSupplier localDateTimeSupplier;

    @Test
    void shouldMapFilingReceivedItemAndTransactionToMessageSend() {
        // given
        FilingReceived payload = buildPayload();
        uk.gov.companieshouse.filing.received.Transaction item = buildItem();
        Transaction transaction = buildTransaction();

        when(filingReceivedTemplateMapper.mapDescriptionTemplates(any(), any()))
                .thenReturn(new DescriptionTemplate("mappedDesc", "template"));
        when(randomNumberGenerator.random()).thenReturn("12345");
        when(filingReceivedDataMapper.map(any(), any(), any(), any(), any())).thenReturn(new MessageSendData());
        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.of(2026, 4, 28, 12, 30, 30));

        MessageSend expected = buildMessageSend();

        // when
        MessageSend actual = filingReceivedMapper.map(payload, item, transaction);

        // then
        assertEquals(expected, actual);
        verify(filingReceivedTemplateMapper).mapDescriptionTemplates("originalDesc", item);
        verify(randomNumberGenerator).random();
        verify(filingReceivedDataMapper).map(transaction, "originalDesc", payload, "mappedDesc", item);
    }

    private static MessageSend buildMessageSend() {
        return new MessageSend()
                .appId("filing_received_notification_sender." + "template")
                .messageType("template")
                .messageId("<transactionId.12345@companieshouse.gov.uk>")
                .data(new MessageSendData())
                .createdAt("2026-04-28T12:30:30")
                .userId("userId");
    }

    private static FilingReceived buildPayload() {
        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("transactionId");

        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setUserId("userId");

        FilingReceived payload = new FilingReceived();
        payload.setSubmission(submissionRecord);
        payload.setPresenter(presenterRecord);

        return payload;
    }

    private uk.gov.companieshouse.filing.received.Transaction buildItem() {
        uk.gov.companieshouse.filing.received.Transaction item = new uk.gov.companieshouse.filing.received.Transaction();
        item.setSubmissionId("submissionId");
        return item;
    }

    private static Transaction buildTransaction() {
        Filing filing = new Filing();
        filing.setDescription("originalDesc");

        Transaction transaction = new Transaction();
        transaction.setFilings(Map.of("submissionId", filing));

        return transaction;
    }
}