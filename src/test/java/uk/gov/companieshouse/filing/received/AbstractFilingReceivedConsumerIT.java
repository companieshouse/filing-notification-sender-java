package uk.gov.companieshouse.filing.received;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.companieshouse.filing.common.AbstractConsumerIT;
import uk.gov.companieshouse.filing.received.mapper.LocalDateTimeSupplier;

abstract class AbstractFilingReceivedConsumerIT extends AbstractConsumerIT {

    static final String TRANSACTIONS_API_RESPONSE_PATH = "/received/transaction-response.json";
    static final String FILING_RECEIVED_PATH = "/received/filing-received.json";
    static final String FILING_RECEIVED_MULTIPLE_PATH = "/received/filing-received-multiple.json";
    static final String MESSAGE_SEND_PATH = "/received/message-send.json";

    @MockitoBean
    private LocalDateTimeSupplier localDateTimeSupplier;

    AbstractFilingReceivedConsumerIT() {
        super("filing-received");
    }

    @BeforeEach
    void setUp() {
        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse("2026-04-16T10:20:26"));
    }

    static byte[] buildFilingReceivedBytes() throws IOException {
        FilingReceived message = readAvroJson(FILING_RECEIVED_PATH, FilingReceived.class, FilingReceived.SCHEMA$);
        return writePayloadToBytes(message, FilingReceived.class);
    }

    static byte[] buildFilingReceivedMultipleBytes() throws IOException {
        FilingReceived message = readAvroJson(FILING_RECEIVED_MULTIPLE_PATH, FilingReceived.class, FilingReceived.SCHEMA$);
        return writePayloadToBytes(message, FilingReceived.class);
    }
}
