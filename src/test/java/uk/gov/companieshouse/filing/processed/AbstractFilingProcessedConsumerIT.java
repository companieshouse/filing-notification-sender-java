package uk.gov.companieshouse.filing.processed;

import java.io.IOException;
import uk.gov.companieshouse.filing.common.AbstractConsumerIT;

abstract class AbstractFilingProcessedConsumerIT extends AbstractConsumerIT {

    static final String FILING_PROCESSED_ACCEPTED_PATH = "/processed/filing-processed-accepted.json";
    static final String FILING_PROCESSED_REJECTED_PATH = "/processed/filing-processed-rejected.json";
    static final String MESSAGE_SEND_ACCEPTED_PATH = "/processed/message-send-accepted.json";
    static final String MESSAGE_SEND_REJECTED_PATH = "/processed/message-send-rejected.json";

    AbstractFilingProcessedConsumerIT() {
        super("filing-processed");
    }

    static byte[] buildFilingProcessedAcceptedBytes() throws IOException {
        FilingProcessed message = readAvroJson(FILING_PROCESSED_ACCEPTED_PATH, FilingProcessed.class, FilingProcessed.SCHEMA$);
        return writePayloadToBytes(message, FilingProcessed.class);
    }

    static byte[] buildFilingProcessedRejectedBytes() throws IOException {
        FilingProcessed message = readAvroJson(FILING_PROCESSED_REJECTED_PATH, FilingProcessed.class, FilingProcessed.SCHEMA$);
        return writePayloadToBytes(message, FilingProcessed.class);
    }
}
