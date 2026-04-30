package uk.gov.companieshouse.filing.common.mapper;

import static uk.gov.companieshouse.filing.Application.NAMESPACE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class DateMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TWELVE_HOUR_PERIOD_FORMATTER = DateTimeFormatter.ofPattern("h:mma");

    public String formatFullMonth(String unformattedDate, boolean doubleDigitDay) {
        try {
            LocalDate date = LocalDate.parse(unformattedDate, DATE_FORMATTER);
            int day = date.getDayOfMonth();
            String fullMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            int year = date.getYear();

            if (doubleDigitDay) {
                return "%02d %s %d".formatted(day, fullMonth, year);
            } else {
                return "%d %s %d".formatted(day, fullMonth, year);
            }
        } catch (DateTimeParseException ex) {
            LOGGER.error("Error parsing date: [%s]".formatted(unformattedDate), ex);
            throw new InvalidPayloadException("Invalid date format", ex);
        }
    }

    public String formatTimePeriod(String unformattedDate) {
        try {
            return Instant.parse(unformattedDate)
                    .atZone(ZoneId.of("Europe/London"))
                    .format(TWELVE_HOUR_PERIOD_FORMATTER)
                    .toLowerCase();
        } catch (DateTimeParseException ex) {
            LOGGER.error("Error parsing date: [%s]".formatted(unformattedDate), ex);
            throw new InvalidPayloadException("Invalid date format", ex);
        }
    }
}
