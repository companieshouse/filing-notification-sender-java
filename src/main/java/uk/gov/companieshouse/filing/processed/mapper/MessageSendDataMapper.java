package uk.gov.companieshouse.filing.processed.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;

@Component
public class MessageSendDataMapper {

    private final String chsUrl;
    private final DateMapper dateMapper;

    public MessageSendDataMapper(@Value("${api.url.chs}") String chsUrl, DateMapper dateMapper) {
        this.chsUrl = chsUrl;
        this.dateMapper = dateMapper;
    }

    MessageSendData map(Transaction transaction, String originalDescription, FilingProcessed payload,
            String mappedDescription) {
        ResponseRecord responseRecord = payload.getResponse();
        String companyName = transaction.getCompanyName();
        if (originalDescription.contains("insolvency") ||
                (originalDescription.contains("Change of registered office address") && StringUtils.isBlank(companyName))) {
            companyName = responseRecord.getCompanyName();
        }

        String subject = "%s %s for %s (%s)".formatted(mappedDescription, responseRecord.getStatus(), companyName,
                responseRecord.getCompanyNumber());

        MessageSendData data = new MessageSendData()
                .chsURL(chsUrl)
                .companyName(companyName)
                .companyNumber(responseRecord.getCompanyNumber())
                .filingDescription(mappedDescription)
                .to(payload.getPresenter().getUserId())
                .subject(subject)
                .processedAt(dateMapper.formatFullMonth(responseRecord.getProcessedAt().substring(0, 10)))
                .status(responseRecord.getStatus())
                .transactionId(payload.getSubmission().getTransactionId());

        RejectRecord reject = responseRecord.getReject();
        if (reject != null) {
            data.rejectReasonsEnglish(reject.getReasonsEnglish())
                    .rejectReasonsWelsh(reject.getReasonsWelsh());
        }
        return data;
    }
}
