package uk.gov.companieshouse.filing.received.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.filing.common.mapper.RandomNumberGenerator;
import uk.gov.companieshouse.filing.received.FilingReceived;

@Component
public class FilingReceivedMapper {

    private static final String APP_ID = "filing_received_notification_sender";
    private static final String EMAIL_SUFFIX = "@companieshouse.gov.uk>";

    private final FilingReceivedTemplateMapper filingReceivedTemplateMapper;
    private final RandomNumberGenerator randomNumberGenerator;
    private final FilingReceivedDataMapper filingReceivedDataMapper;
    private final LocalDateTimeSupplier localDateTimeSupplier;

    public FilingReceivedMapper(FilingReceivedTemplateMapper filingReceivedTemplateMapper,
            RandomNumberGenerator randomNumberGenerator,
            FilingReceivedDataMapper filingReceivedDataMapper, LocalDateTimeSupplier localDateTimeSupplier) {
        this.filingReceivedTemplateMapper = filingReceivedTemplateMapper;
        this.randomNumberGenerator = randomNumberGenerator;
        this.filingReceivedDataMapper = filingReceivedDataMapper;
        this.localDateTimeSupplier = localDateTimeSupplier;
    }

    public MessageSend map(FilingReceived payload, uk.gov.companieshouse.filing.received.Transaction item,
            uk.gov.companieshouse.api.model.transaction.Transaction transaction) {

        String originalDesc = transaction.getFilings().get(item.getSubmissionId()).getDescription();

        DescriptionTemplate descriptionTemplate = filingReceivedTemplateMapper.mapDescriptionTemplates(originalDesc, item);

        String randomNumber = randomNumberGenerator.random();
        String messageId = "<" + payload.getSubmission().getTransactionId() + "." + randomNumber + EMAIL_SUFFIX;

        MessageSendData data = filingReceivedDataMapper.map(transaction, originalDesc, payload,
                descriptionTemplate.mappedDescription(), item);
        return new MessageSend()
                .appId(APP_ID + "." + descriptionTemplate.template())
                .messageType(descriptionTemplate.template())
                .messageId(messageId)
                .data(data)
                .createdAt(localDateTimeSupplier.get().toString())
                .userId(payload.getPresenter().getUserId());
    }
}
