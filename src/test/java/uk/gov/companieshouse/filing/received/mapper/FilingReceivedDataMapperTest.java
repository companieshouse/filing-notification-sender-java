package uk.gov.companieshouse.filing.received.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.mapper.DateMapper;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class FilingReceivedDataMapperTest {

    private static final String CHS_URL = "http://chs-url";

    private FilingReceivedDataMapper mapper;
    @Mock
    private DateMapper dateMapper;

    @BeforeEach
    void setUp() {
        mapper = new FilingReceivedDataMapper(CHS_URL, dateMapper);
    }

    @Test
    void shouldMapMessageSendData() {
        // given
        Transaction transaction = buildTransaction();
        uk.gov.companieshouse.filing.received.Transaction item = buildItem("kind");
        FilingReceived payload = buildPayload();

        when(dateMapper.formatFullMonth(any(), anyBoolean())).thenReturn("20 April 2026");
        when(dateMapper.formatTimePeriod(any())).thenReturn("12:30pm");

        MessageSendData expected = buildMessageSendData("transactionCompanyName");

        // when
        MessageSendData actual = mapper.map(transaction, "originalDesc", payload, "mappedDesc", item);

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20", true);
        verify(dateMapper).formatTimePeriod("2026-04-20T12:30:00Z");
    }

    @Test
    void shouldMapMessageSendDataAcceptedVaryingCompanyNames() {
        // given
        Transaction transaction = buildTransaction();
        uk.gov.companieshouse.filing.received.Transaction item = buildItem("kind");
        FilingReceived payload = buildPayload();

        when(dateMapper.formatFullMonth(any(), anyBoolean())).thenReturn("20 April 2026");
        when(dateMapper.formatTimePeriod(any())).thenReturn("12:30pm");

        MessageSendData expected = buildMessageSendData("submissionCompanyName");

        // when
        MessageSendData actual = mapper.map(transaction, "insolvency original desc", payload, "mappedDesc", item);

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20", true);
        verify(dateMapper).formatTimePeriod("2026-04-20T12:30:00Z");
    }

    @Test
    void shouldMapMessageSendDataKindROA() {
        // given
        Transaction transaction = buildTransaction();
        uk.gov.companieshouse.filing.received.Transaction item = buildItem("registered-office-address#LIQAD01");
        FilingReceived payload = buildPayload();

        when(dateMapper.formatFullMonth(any(), anyBoolean())).thenReturn("20 April 2026");
        when(dateMapper.formatTimePeriod(any())).thenReturn("12:30pm");

        MessageSendData expected = buildMessageSendData("submissionCompanyName");

        // when
        MessageSendData actual = mapper.map(transaction, "originalDesc", payload, "mappedDesc", item);

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20", true);
        verify(dateMapper).formatTimePeriod("2026-04-20T12:30:00Z");
    }

    private static MessageSendData buildMessageSendData(String companyName) {
        MessageSendData data = new MessageSendData();
        data.chsUrl(CHS_URL);
        data.companyName(companyName);
        data.companyNumber("12345678");
        data.filingDescription("mappedDesc");
        data.to("presenterUserId");
        data.subject("mappedDesc received for " + companyName + " (12345678)");
        data.receivedAtDate("20 April 2026");
        data.receivedAtTime("12:30pm");
        data.transactionId("transactionId");
        return data;
    }

    private static uk.gov.companieshouse.filing.received.Transaction buildItem(String kind) {
        uk.gov.companieshouse.filing.received.Transaction item = new uk.gov.companieshouse.filing.received.Transaction();
        item.setKind(kind);
        return item;
    }

    private static Transaction buildTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCompanyName("transactionCompanyName");
        return transaction;
    }

    private static FilingReceived buildPayload() {
        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setUserId("presenterUserId");

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("transactionId");
        submissionRecord.setReceivedAt("2026-04-20T12:30:00Z");
        submissionRecord.setCompanyName("submissionCompanyName");
        submissionRecord.setCompanyNumber("12345678");

        FilingReceived filingReceived = new FilingReceived();
        filingReceived.setPresenter(presenterRecord);
        filingReceived.setSubmission(submissionRecord);

        return filingReceived;
    }
}