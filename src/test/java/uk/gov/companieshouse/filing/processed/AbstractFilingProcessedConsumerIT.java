package uk.gov.companieshouse.filing.processed;

import java.util.List;
import uk.gov.companieshouse.filing.common.AbstractConsumerIT;

abstract class AbstractFilingProcessedConsumerIT extends AbstractConsumerIT {

    static final String MAIN_TOPIC = "filing-processed";
    static final String RETRY_TOPIC = "filing-processed-filing-notification-sender-retry";
    static final String ERROR_TOPIC = "filing-processed-filing-notification-sender-error";
    static final String INVALID_TOPIC = "filing-processed-filing-notification-sender-invalid";

    @Override
    protected List<String> getSubscribedTopics() {
        return List.of(MAIN_TOPIC, RETRY_TOPIC, ERROR_TOPIC, INVALID_TOPIC);
    }

    static FilingProcessed buildFilingProcessed() {
        PresenterRecord presenter = new PresenterRecord();
        presenter.setLanguage("ENG");
        presenter.setUserId("654852");

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("987654");

        FilingProcessed filingProcessed = new FilingProcessed();
        filingProcessed.setApplicationId("1234");
        filingProcessed.setAttempt(0);
        filingProcessed.setChannelId("123");
        filingProcessed.setResponse(buildResponseRecord());
        filingProcessed.setPresenter(presenter);
        filingProcessed.setSubmission(submissionRecord);

        return filingProcessed;
    }

    private static ResponseRecord buildResponseRecord() {
        RejectRecord rejectRecord = new RejectRecord();
        rejectRecord.setReasonsEnglish(List.of("Reason 1", "Reason 2"));
        rejectRecord.setReasonsWelsh(List.of("Rheswm 1", "Rheswm 2"));

        ResponseRecord responseRecord = new ResponseRecord();
        responseRecord.setReject(rejectRecord);
        responseRecord.setCompanyName("dummy company");
        responseRecord.setCompanyNumber("123456");
        responseRecord.setDateOfCreation("2026-02-17T13:34:50");
        responseRecord.setProcessedAt("2026-02-17T16:34:50Z");
        responseRecord.setStatus("submitted");
        responseRecord.setSubmissionId("987654-1");
        return responseRecord;
    }
}
