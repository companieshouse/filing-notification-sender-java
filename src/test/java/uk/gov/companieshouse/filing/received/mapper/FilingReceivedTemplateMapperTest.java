package uk.gov.companieshouse.filing.received.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.companieshouse.filing.received.Transaction;

class FilingReceivedTemplateMapperTest {

    private static final Transaction ITEM = new Transaction();

    private final FilingReceivedTemplateMapper mapper = new FilingReceivedTemplateMapper();

    @ParameterizedTest
    @CsvSource({
            "Some insolvency, 600, Notice of appointment of liquidator in a creditors' voluntary winding up (600)",
            "insolvency, LRESEX, Extraordinary resolution to wind up (LRESEX)",
            "insolvency, LIQ02, Notice of Statement of Affairs (LIQ02)",
            "insolvency, LIQ03, Notice of Progress Report (LIQ03)",
            "insolvency, unknown, insolvency"
    })
    void mapsInsolvencyDescriptions(String description, String kind, String expectedDescription) {
        Transaction item = new Transaction();
        item.setKind(kind);
        DescriptionTemplate actual = mapper.mapDescriptionTemplates(description, item);
        assertEquals(expectedDescription, actual.mappedDescription());
        assertEquals("filing_received_email_insolvency", actual.template());
    }

    @Test
    void mapsPackageAccounts() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts for year end", ITEM);
        assertEquals("Package accounts for year end", actual.mappedDescription());
        assertEquals("filing_received_email_package_accounts", actual.template());
    }

    @Test
    void mapsAcspApplicationUpdate() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application update", ITEM);
        assertEquals("acsp application update", actual.mappedDescription());
        assertEquals("filing_received_email_update_acsp", actual.template());
    }

    @Test
    void mapsAcspApplicationClose() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application close", ITEM);
        assertEquals("acsp application close", actual.mappedDescription());
        assertEquals("filing_received_email_close_acsp", actual.template());
    }

    @Test
    void mapsAcspApplicationRegister() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application", ITEM);
        assertEquals("acsp application", actual.mappedDescription());
        assertEquals("filing_received_email_acsp_registration", actual.template());
    }

    @Test
    void mapsDefault() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some unrelated description", ITEM);
        assertEquals("Some unrelated description", actual.mappedDescription());
        assertEquals("filing_received_email", actual.template());
    }
}
