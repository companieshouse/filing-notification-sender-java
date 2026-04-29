package uk.gov.companieshouse.filing.processed.mapper;

import static uk.gov.companieshouse.filing.Application.NAMESPACE;

import java.time.LocalDate;
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

    String formatFullMonth(String unformattedDate) {
        try {
            LocalDate date = LocalDate.parse(unformattedDate, DATE_FORMATTER);
            int day = date.getDayOfMonth();
            String fullMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            int year = date.getYear();
            return String.format("%d %s %d", day, fullMonth, year);
        } catch (DateTimeParseException ex) {
            LOGGER.error("Error parsing date: [%s]".formatted(unformattedDate), ex);
            throw new InvalidPayloadException("Invalid date format", ex);
        }
    }
}
