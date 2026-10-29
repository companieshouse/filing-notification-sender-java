package uk.gov.companieshouse.filing.processed.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.companieshouse.api.model.transaction.Filing;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilingProcessedTemplateMapperTest {

    private final FilingProcessedTemplateMapper mapper = new FilingProcessedTemplateMapper();

    @Test
    void mapsInsolvency600() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some insolvency 600", new Filing());
        assertEquals("Notice of appointment of liquidator in a creditors' voluntary winding up (600)",
                actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLRESEX() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LRESEX", new Filing());
        assertEquals("Extraordinary resolution to wind up (LRESEX)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ02() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ02", new Filing());
        assertEquals("Notice of Statement of Affairs (LIQ02)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ03() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ03", new Filing());
        assertEquals("Notice of Progress Report (LIQ03)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyUnknown() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency something else", new Filing());
        assertEquals("insolvency something else", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsPackageAccounts() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts for year end", new Filing());
        assertEquals("Package accounts for year end", actual.mappedDescription());
        assertEquals("filing_accepted_package_accounts", actual.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts", actual.rejectedTemplate());
    }

    @Test
    void mapsPackageAccountsOversea() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts with package type overseas", new Filing());
        assertEquals("Package accounts with package type overseas", actual.mappedDescription());
        assertEquals("filing_accepted_package_accounts_oversea", actual.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts_oversea", actual.rejectedTemplate());
    }

    @Test
    void mapsRegisteredEmailAddress() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Registered Email Address change", new Filing());
        assertEquals("Registered Email Address change", actual.mappedDescription());
        assertEquals("filing_processed_email_registered_email_address", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationUpdate() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application update", new Filing());
        assertEquals("acsp application update", actual.mappedDescription());
        assertEquals("filing_accepted_update_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_update_acsp", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationClose() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application close", new Filing());
        assertEquals("acsp application close", actual.mappedDescription());
        assertEquals("filing_accepted_close_acsp", actual.acceptedTemplate());
        assertEquals("", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationRegister() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application", new Filing());
        assertEquals("acsp application", actual.mappedDescription());
        assertEquals("filing_accepted_register_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_register_acsp", actual.rejectedTemplate());
    }

    @ParameterizedTest
    @CsvSource({
            "Application to strike off and dissolve a company, dissolution#ds01, filing_processed_email_ds01, filing_rejected_email_ds01",
            "Application to strike off and dissolve a company, dissolution#llds01, filing_processed_email_llds01, filing_rejected_email_llds01",
            "Application to strike off and dissolve a company, dissolution#lpds01, filing_processed_email_ds01, filing_rejected_email_ds01",
            "Application to strike off and dissolve a company, dissolution#DS01, filing_processed_email_ds01, filing_rejected_email_ds01"
    })
    void mapsDissolution(String description, String kind, String expectedAcceptedTemplate, String expectedRejectedTemplate) {
        Filing filing = new Filing();
        filing.setType(kind);
        DescriptionTemplate actual = mapper.mapDescriptionTemplates(description, filing);
        assertEquals("Application to strike off and dissolve a company", actual.mappedDescription());
        assertEquals(expectedAcceptedTemplate, actual.acceptedTemplate());
        assertEquals(expectedRejectedTemplate, actual.rejectedTemplate());
    }

    @Test
    void mapsDefault() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some unrelated description", new Filing());
        assertEquals("Some unrelated description", actual.mappedDescription());
        assertEquals("filing_processed_email", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }
}
