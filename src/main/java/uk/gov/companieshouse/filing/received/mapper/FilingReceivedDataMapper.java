package uk.gov.companieshouse.filing.received.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.mapper.DateMapper;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.SubmissionRecord;

@Component
public class FilingReceivedDataMapper {

    private final String chsUrl;
    private final DateMapper dateMapper;

    public FilingReceivedDataMapper(@Value("${api.url.chs}") String chsUrl, DateMapper dateMapper) {
        this.chsUrl = chsUrl;
        this.dateMapper = dateMapper;
    }

    MessageSendData map(Transaction transaction, String originalDescription, FilingReceived payload, String mappedDescription,
            uk.gov.companieshouse.filing.received.Transaction item) {
        SubmissionRecord submissionRecord = payload.getSubmission();
        String companyName = transaction.getCompanyName();
        if (originalDescription.contains("insolvency") || item.getKind().contains("registered-office-address#LIQAD01")) {
            companyName = submissionRecord.getCompanyName();
        }

        String subject = "%s received for %s (%s)".formatted(mappedDescription, companyName, submissionRecord.getCompanyNumber());

        return new MessageSendData()
                .chsUrl(chsUrl)
                .companyName(companyName)
                .companyNumber(submissionRecord.getCompanyNumber())
                .filingDescription(mappedDescription)
                .to(payload.getPresenter().getUserId())
                .subject(subject)
                .receivedAtDate(dateMapper.formatFullMonth(submissionRecord.getReceivedAt().substring(0, 10), true))
                .receivedAtTime(dateMapper.formatTimePeriod(submissionRecord.getReceivedAt()))
                .transactionId(payload.getSubmission().getTransactionId());
    }
}
