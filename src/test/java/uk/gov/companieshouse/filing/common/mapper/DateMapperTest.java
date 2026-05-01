package uk.gov.companieshouse.filing.common.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.filing.common.exception.InvalidPayloadException;

class DateMapperTest {

    private final DateMapper mapper = new DateMapper();

    @Test
    void formatsValidDate() {
        String formatted = mapper.formatFullMonth("2026-04-20", false);
        assertEquals("20 April 2026", formatted);
    }

    @Test
    void formatsSingleDigitDay() {
        String formatted = mapper.formatFullMonth("2026-01-05", false);
        assertEquals("5 January 2026", formatted);
    }

    @Test
    void formatsDoubleDigitDayLeadingZero() {
        String formatted = mapper.formatFullMonth("2026-01-05", true);
        assertEquals("05 January 2026", formatted);
    }

    @Test
    void fullMonthThrowsOnInvalidDate() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatFullMonth("not-a-date", false));
    }

    @Test
    void fullMonthThrowsOnWrongFormat() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatFullMonth("20-04-2026", false));
    }

    @Test
    void formatsAmTimePeriod() {
        assertEquals("4:05am", mapper.formatTimePeriod("2026-04-16T03:05:45Z"));
    }

    @Test
    void formatsPmTimePeriod() {
        assertEquals("11:15pm", mapper.formatTimePeriod("2026-02-16T23:15:45Z"));
    }

    @Test
    void timePeriodThrowsOnInvalidDate() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatTimePeriod("not-a-date"));
    }

    @Test
    void timePeriodThrowsOnWrongFormat() {
        assertThrows(InvalidPayloadException.class, () -> mapper.formatTimePeriod("16-04-2026T23:05:45Z"));
    }
}
