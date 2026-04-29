package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.filing.common.exception.InvalidPayloadException;

class DateMapperTest {

    private final DateMapper mapper = new DateMapper();

    @Test
    void formatsValidDate() {
        String formatted = mapper.formatFullMonth("2026-04-20");
        assertEquals("20 April 2026", formatted);
    }

    @Test
    void formatsSingleDigitDay() {
        String formatted = mapper.formatFullMonth("2026-01-05");
        assertEquals("5 January 2026", formatted);
    }

    @Test
    void throwsOnInvalidDate() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatFullMonth("not-a-date"));
    }

    @Test
    void throwsOnWrongFormat() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatFullMonth("20-04-2026"));
    }
}
