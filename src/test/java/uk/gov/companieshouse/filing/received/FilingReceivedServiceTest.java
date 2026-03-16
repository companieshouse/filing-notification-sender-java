package uk.gov.companieshouse.filing.received;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class FilingReceivedServiceTest {

    private final FilingReceivedService filingReceivedService = new FilingReceivedService();

    @Test
    void shouldHandlePayloadSuccessfully() {
        assertDoesNotThrow(() -> filingReceivedService.handlePayload(new FilingReceived()));
    }
}