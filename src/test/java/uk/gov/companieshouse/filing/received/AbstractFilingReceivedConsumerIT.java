package uk.gov.companieshouse.filing.received;

import java.util.List;
import uk.gov.companieshouse.filing.common.AbstractConsumerIT;

abstract class AbstractFilingReceivedConsumerIT extends AbstractConsumerIT {

    AbstractFilingReceivedConsumerIT() {
        super("filing-received");
    }

    static FilingReceived buildFilingReceived() {
        PresenterRecord presenter = new PresenterRecord();
        presenter.setLanguage("ENG");
        presenter.setUserId("654852");
        presenter.setForename("forename");
        presenter.setSurname("surname");

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId("021787-298317-763347");
        submissionRecord.setCompanyName("companyName");
        submissionRecord.setCompanyNumber("12345678");
        submissionRecord.setReceivedAt("2026-02-17T16:34:50Z");

        FilingReceived filingReceived = new FilingReceived();
        filingReceived.setApplicationId("1234");
        filingReceived.setAttempt(0);
        filingReceived.setChannelId("123");
        filingReceived.setItems(buildItems());
        filingReceived.setPresenter(presenter);
        filingReceived.setSubmission(submissionRecord);

        return filingReceived;
    }

    private static List<Transaction> buildItems() {
        Transaction transaction1 = new Transaction();
        transaction1.setData("data");
        transaction1.setKind("kind");
        transaction1.setSubmissionId("submissionId1");
        transaction1.setSubmissionLanguage("ENG");

        Transaction transaction2 = new Transaction();
        transaction2.setData("data");
        transaction2.setKind("kind");
        transaction2.setSubmissionId("submissionId2");
        transaction2.setSubmissionLanguage("ENG");
        return List.of(transaction1, transaction2);
    }
}
