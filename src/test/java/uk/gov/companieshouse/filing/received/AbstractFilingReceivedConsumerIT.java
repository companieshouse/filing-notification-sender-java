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
        submissionRecord.setTransactionId("987654");
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
        Transaction transaction = new Transaction();
        transaction.setData("data");
        transaction.setKind("kind");
        transaction.setSubmissionId("submissionId");
        transaction.setSubmissionLanguage("ENG");
        return List.of(transaction);
    }
}
