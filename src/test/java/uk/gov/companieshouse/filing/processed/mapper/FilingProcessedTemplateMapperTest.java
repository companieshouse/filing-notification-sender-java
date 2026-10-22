package uk.gov.companieshouse.filing.processed.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.testdata.TransactionTestDataBuilder;

class FilingProcessedTemplateMapperTest {

    private final FilingProcessedTemplateMapper mapper = new FilingProcessedTemplateMapper();

    @Test
    void mapsInsolvency600() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some insolvency 600", transaction);
        assertEquals("Notice of appointment of liquidator in a creditors' voluntary winding up (600)",
                actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLRESEX() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LRESEX", transaction);
        assertEquals("Extraordinary resolution to wind up (LRESEX)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ02() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ02", transaction);
        assertEquals("Notice of Statement of Affairs (LIQ02)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyLIQ03() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency LIQ03", transaction);
        assertEquals("Notice of Progress Report (LIQ03)", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsInsolvencyUnknown() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("insolvency something else", transaction);
        assertEquals("insolvency something else", actual.mappedDescription());
        assertEquals("filing_accepted_email_insolvency", actual.acceptedTemplate());
        assertEquals("filing_rejected_email_insolvency", actual.rejectedTemplate());
    }

    @Test
    void mapsPackageAccounts() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts for year end", transaction);
        assertEquals("Package accounts for year end", actual.mappedDescription());
        assertEquals("filing_accepted_package_accounts", actual.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts", actual.rejectedTemplate());
    }

    @Test
    void mapsPackageAccountsOversea() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Package accounts with package type overseas", transaction);
        assertEquals("Package accounts with package type overseas", actual.mappedDescription());
        assertEquals("filing_accepted_package_accounts_oversea", actual.acceptedTemplate());
        assertEquals("filing_rejected_package_accounts_oversea", actual.rejectedTemplate());
    }

    @Test
    void mapsRegisteredEmailAddress() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Registered Email Address change", transaction);
        assertEquals("Registered Email Address change", actual.mappedDescription());
        assertEquals("filing_processed_email_registered_email_address", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationUpdate() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application update", transaction);
        assertEquals("acsp application update", actual.mappedDescription());
        assertEquals("filing_accepted_update_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_update_acsp", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationClose() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application close", transaction);
        assertEquals("acsp application close", actual.mappedDescription());
        assertEquals("filing_accepted_close_acsp", actual.acceptedTemplate());
        assertEquals("", actual.rejectedTemplate());
    }

    @Test
    void mapsAcspApplicationRegister() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("acsp application", transaction);
        assertEquals("acsp application", actual.mappedDescription());
        assertEquals("filing_accepted_register_acsp", actual.acceptedTemplate());
        assertEquals("filing_rejected_register_acsp", actual.rejectedTemplate());
    }

    @ParameterizedTest
    @CsvSource({
            "Dissolution application to strike off and dissolve a company, dissolution#ds01, filing_processed_email_ds01, filing_rejected_email_ds01",
            "Dissolution application to strike off and dissolve a company, dissolution#llds01, filing_processed_email_llds01, filing_rejected_email_llds01",
            "Dissolution application to strike off and dissolve a company, dissolution#lpds01, filing_processed_email_ds01, filing_rejected_email_ds01",
            "Dissolution application to strike off and dissolve a company, dissolution#DS01, filing_processed_email_ds01, filing_rejected_email_ds01"
    })
    void mapsDissolution(String description, String kind, String expectedAcceptedTemplate, String expectedRejectedTemplate) {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().withKind(kind).build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates(description, transaction);
        assertEquals("Dissolution application to strike off and dissolve a company", actual.mappedDescription());
        assertEquals(expectedAcceptedTemplate, actual.acceptedTemplate());
        assertEquals(expectedRejectedTemplate, actual.rejectedTemplate());
    }

    @Test
    void mapsDefault() {
        Transaction transaction = TransactionTestDataBuilder.aTransaction().build();
        DescriptionTemplate actual = mapper.mapDescriptionTemplates("Some unrelated description", transaction);
        assertEquals("Some unrelated description", actual.mappedDescription());
        assertEquals("filing_processed_email", actual.acceptedTemplate());
        assertEquals("filing_rejected_email", actual.rejectedTemplate());
    }
}
