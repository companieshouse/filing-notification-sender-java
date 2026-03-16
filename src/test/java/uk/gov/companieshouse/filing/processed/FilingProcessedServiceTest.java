package uk.gov.companieshouse.filing.processed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class FilingProcessedServiceTest {

    private final FilingProcessedService filingProcessedService = new FilingProcessedService();

    @Test
    void shouldHandlePayloadSuccessfully() {
        assertDoesNotThrow(() -> filingProcessedService.handlePayload(new FilingProcessed()));
    }
}