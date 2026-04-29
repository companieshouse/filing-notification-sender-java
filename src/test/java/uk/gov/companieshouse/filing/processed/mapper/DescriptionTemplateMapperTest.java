package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DescriptionTemplateMapperTest {

    private final DescriptionTemplateMapper mapper = new DescriptionTemplateMapper();

    @Test
    void mapsInsolvency600() {
        var result = mapper.mapDescriptionTemplates("Some insolvency 600");
        assertEquals("Notice of appointment of liquidator in a creditors' voluntary winding up (600)",
                result.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", result.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", result.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLRESEX() {
        var result = mapper.mapDescriptionTemplates("insolvency LRESEX");
        assertEquals("Extraordinary resolution to wind up (LRESEX)", result.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", result.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", result.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ02() {
        var result = mapper.mapDescriptionTemplates("insolvency LIQ02");
        assertEquals("Notice of Statement of Affairs (LIQ02)", result.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", result.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", result.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ03() {
        var result = mapper.mapDescriptionTemplates("insolvency LIQ03");
        assertEquals("Notice of Progress Report (LIQ03)", result.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", result.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", result.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyUnknown() {
        var result = mapper.mapDescriptionTemplates("insolvency something else");
        assertEquals("insolvency something else", result.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", result.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", result.rejectedTemplate());
    }

    @Test
    void mapsPackageAccounts() {
        var result = mapper.mapDescriptionTemplates("Package accounts for year end");
        assertEquals("Package accounts for year end", result.mappedDescription());
        assertEquals("filing_accepted_package_accounts", result.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts", result.rejectedTemplate());
    }

    @Test
    void mapsRegisteredEmailAddress() {
        var result = mapper.mapDescriptionTemplates("Registered Email Address change");
        assertEquals("Registered Email Address change", result.mappedDescription());
        assertEquals("filing_processed_email_registered_email_address", result.acceptedTemplate());
        assertEquals("filing_rejected_email", result.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationUpdate() {
        var result = mapper.mapDescriptionTemplates("acsp application update");
        assertEquals("acsp application update", result.mappedDescription());
        assertEquals("filing_accepted_update_acsp", result.acceptedTemplate());
        assertEquals("filing_rejected_update_acsp", result.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationClose() {
        var result = mapper.mapDescriptionTemplates("acsp application close");
        assertEquals("acsp application close", result.mappedDescription());
        assertEquals("filing_accepted_close_acsp", result.acceptedTemplate());
        assertEquals("", result.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationRegister() {
        var result = mapper.mapDescriptionTemplates("acsp application");
        assertEquals("acsp application", result.mappedDescription());
        assertEquals("filing_accepted_register_acsp", result.acceptedTemplate());
        assertEquals("filing_rejected_register_acsp", result.rejectedTemplate());
    }

    @Test
    void mapsDefault() {
        var result = mapper.mapDescriptionTemplates("Some unrelated description");
        assertEquals("Some unrelated description", result.mappedDescription());
        assertEquals("filing_processed_email", result.acceptedTemplate());
        assertEquals("filing_rejected_email", result.rejectedTemplate());
    }
}
