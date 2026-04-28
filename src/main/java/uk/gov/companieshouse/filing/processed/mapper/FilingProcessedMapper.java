package uk.gov.companieshouse.filing.processed.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.mapper.RandomNumberGenerator;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.ResponseRecord;

@Component
public class FilingProcessedMapper {

    private static final String APP_ID = "filing_processed_notification_sender";
    private static final String EMAIL_SUFFIX = "@companieshouse.gov.uk>";

    private final FilingProcessedTemplateMapper filingProcessedTemplateMapper;
    private final RandomNumberGenerator randomNumberGenerator;
    private final FilingProcessedDataMapper filingProcessedDataMapper;

    public FilingProcessedMapper(FilingProcessedTemplateMapper filingProcessedTemplateMapper,
            RandomNumberGenerator randomNumberGenerator,
            FilingProcessedDataMapper filingProcessedDataMapper) {
        this.filingProcessedTemplateMapper = filingProcessedTemplateMapper;
        this.randomNumberGenerator = randomNumberGenerator;
        this.filingProcessedDataMapper = filingProcessedDataMapper;
    }

    public MessageSend map(FilingProcessed payload, Transaction transaction) {
        ResponseRecord responseRecord = payload.getResponse();
        String originalDesc = transaction.getFilings().get(responseRecord.getSubmissionId()).getDescription();

        DescriptionTemplate descriptionTemplate = filingProcessedTemplateMapper.mapDescriptionTemplates(originalDesc);

        String appId;
        String messageType;

        if ("accepted".equals(responseRecord.getStatus())) {
            appId = APP_ID + "." + descriptionTemplate.acceptedTemplate();
            messageType = descriptionTemplate.acceptedTemplate();
        } else {
            appId = APP_ID + "." + descriptionTemplate.rejectedTemplate();
            messageType = descriptionTemplate.rejectedTemplate();
        }

        String randomNumber = randomNumberGenerator.random();
        String messageId = "<" + payload.getSubmission().getTransactionId() + "." + randomNumber + EMAIL_SUFFIX;

        MessageSendData data = filingProcessedDataMapper.map(transaction, originalDesc, payload,
                descriptionTemplate.mappedDescription());
        return new MessageSend()
                .appId(appId)
                .messageType(messageType)
                .messageId(messageId)
                .data(data)
                .createdAt(responseRecord.getDateOfCreation())
                .userId(payload.getPresenter().getUserId());
    }
}
