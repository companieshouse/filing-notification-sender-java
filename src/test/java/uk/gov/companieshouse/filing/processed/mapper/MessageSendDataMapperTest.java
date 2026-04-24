package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class MessageSendDataMapperTest {

    private static final String CHS_URL = "http://chs-url";

    private MessageSendDataMapper mapper;
    @Mock
    private DateMapper dateMapper;

    @BeforeEach
    void setUp() {
        mapper = new MessageSendDataMapper(CHS_URL, dateMapper);
    }

    @Test
    void shouldMapMessageSendDataRejected() {
        // given
        Transaction transaction = buildTransaction();
        FilingProcessed payload = buildPayload("rejected");
        RejectRecord reject = new RejectRecord();
        reject.setReasonsEnglish(List.of("reason1", "reason2"));
        reject.setReasonsWelsh(List.of("rheswm1", "rheswm2"));
        payload.getResponse().setReject(reject);

        when(dateMapper.formatFullMonth(any())).thenReturn("20 April 2026");

        MessageSendData expected = buildMessageSendData("transactionCompanyName", "rejected");
        expected.rejectReasonsEnglish(List.of("reason1", "reason2"));
        expected.rejectReasonsWelsh(List.of("rheswm1", "rheswm2"));

        // when
        MessageSendData actual = mapper.map(transaction, "originalDesc", payload, "mappedDesc");

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20");
    }

    @ParameterizedTest
    @CsvSource({
            "originalDesc, transactionCompanyName",
            "insolvency original desc, responseCompanyName",
            "Change of registered office address, transactionCompanyName"
    })
    void shouldMapMessageSendDataAcceptedVaryingCompanyNames(String description, String expectedCompanyName) {
        // given
        Transaction transaction = buildTransaction();
        FilingProcessed payload = buildPayload("accepted");

        when(dateMapper.formatFullMonth(any())).thenReturn("20 April 2026");

        MessageSendData expected = buildMessageSendData(expectedCompanyName, "accepted");

        // when
        MessageSendData actual = mapper.map(transaction, description, payload, "mappedDesc");

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20");
    }

    @Test
    void shouldMapMessageSendDataAcceptedChangeROAMissingCompanyName() {
        // given
        Transaction transaction = new Transaction();
        FilingProcessed payload = buildPayload("accepted");

        when(dateMapper.formatFullMonth(any())).thenReturn("20 April 2026");

        MessageSendData expected = buildMessageSendData("responseCompanyName", "accepted");

        // when
        MessageSendData actual = mapper.map(transaction, "Change of registered office address", payload, "mappedDesc");

        // then
        assertEquals(expected, actual);
        verify(dateMapper).formatFullMonth("2026-04-20");
    }

    private static MessageSendData buildMessageSendData(String companyName, String status) {
        MessageSendData data = new MessageSendData();
        data.chsUrl(CHS_URL);
        data.companyName(companyName);
        data.companyNumber("12345678");
        data.filingDescription("mappedDesc");
        data.to("presenterUserId");
        data.subject("mappedDesc " + status + " for " + companyName + " (12345678)");
        data.processedAt("20 April 2026");
        data.status(status);
        data.transactionId("transactionId");
        return data;
    }

    private static Transaction buildTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCompanyName("transactionCompanyName");
        return transaction;
    }

    private static FilingProcessed buildPayload(String status) {
        ResponseRecord responseRecord = new ResponseRecord();
        responseRecord.setStatus(status);
        responseRecord.setCompanyNumber("12345678");
        responseRecord.setProcessedAt("2026-04-20T12:00:00Z");
        responseRecord.setCompanyName("responseCompanyName");

        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setUserId("presenterUserId");

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("transactionId");

        FilingProcessed filingProcessed = new FilingProcessed();
        filingProcessed.setResponse(responseRecord);
        filingProcessed.setPresenter(presenterRecord);
        filingProcessed.setSubmission(submissionRecord);

        return filingProcessed;
    }
}