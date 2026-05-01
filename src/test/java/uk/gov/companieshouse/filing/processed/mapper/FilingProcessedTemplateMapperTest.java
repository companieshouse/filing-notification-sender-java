package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilingProcessedTemplateMapperTest {

    private final FilingProcessedTemplateMapper mapper = new FilingProcessedTemplateMapper();

    @Test
    void mapsInsolvency600() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some insolvency 600");
        assertEquals("Notice of appointment of liquidator in a creditors' voluntary winding up (600)",
                actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLRESEX() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LRESEX");
        assertEquals("Extraordinary resolution to wind up (LRESEX)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ02() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ02");
        assertEquals("Notice of Statement of Affairs (LIQ02)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ03() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ03");
        assertEquals("Notice of Progress Report (LIQ03)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyUnknown() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency something else");
        assertEquals("insolvency something else", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsPackageAccounts() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts for year end");
        assertEquals("Package accounts for year end", actual.mappedDescription());
        assertEquals("filing_accepted_package_accounts", actual.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts", actual.rejectedTemplate());
    }

    @Test
    void mapsRegisteredEmailAddress() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Registered Email Address change");
        assertEquals("Registered Email Address change", actual.mappedDescription());
        assertEquals("filing_processed_email_registered_email_address", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationUpdate() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application update");
        assertEquals("acsp application update", actual.mappedDescription());
        assertEquals("filing_accepted_update_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_update_acsp", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationClose() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application close");
        assertEquals("acsp application close", actual.mappedDescription());
        assertEquals("filing_accepted_close_acsp", actual.acceptedTemplate());
        assertEquals("", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationRegister() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application");
        assertEquals("acsp application", actual.mappedDescription());
        assertEquals("filing_accepted_register_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_register_acsp", actual.rejectedTemplate());
    }

    @Test
    void mapsDefault() {
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some unrelated description");
        assertEquals("Some unrelated description", actual.mappedDescription());
        assertEquals("filing_processed_email", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }
}
