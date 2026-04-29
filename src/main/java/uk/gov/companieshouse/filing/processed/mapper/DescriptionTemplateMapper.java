package uk.gov.companieshouse.filing.processed.mapper;

import org.springframework.stereotype.Component;

@Component
public class DescriptionTemplateMapper {

    DescriptionTemplate mapDescriptionTemplates(String description) {
        return switch (description) {
            case String desc when desc.contains("insolvency") -> mapInsolvencyDescriptionTemplates(description);
            case String desc when desc.contains("Package accounts") -> mapPackageAccountsTemplates(description);
            case String desc when desc.contains("Registered Email Address") -> mapRegisteredEmailAddressTemplates(description);
            case String desc when desc.toUpperCase().contains("ACSP APPLICATION") -> mapAcspTemplates(description);
            default -> mapDefaultTemplates(description);
        };
    }

    private static DescriptionTemplate mapInsolvencyDescriptionTemplates(String description) {
        String acceptedTemplate = "filing_accepted_email_insolvency";
        String rejectedTemplate = "filing_rejected_email_insolvency";

        String mappedDescription = switch (description) {
            case String d when d.contains("600") ->
                    "Notice of appointment of liquidator in a creditors' voluntary winding up (600)";
            case String d when d.contains("LRESEX") -> "Extraordinary resolution to wind up (LRESEX)";
            case String d when d.contains("LIQ02") -> "Notice of Statement of Affairs (LIQ02)";
            case String d when d.contains("LIQ03") -> "Notice of Progress Report (LIQ03)";
            default -> description;
        };

        return new DescriptionTemplate(mappedDescription, acceptedTemplate, rejectedTemplate);
    }

    private static DescriptionTemplate mapPackageAccountsTemplates(String description) {
        String acceptedTemplate = "filing_accepted_package_accounts";
        String rejectedTemplate = "filing_rejected_package_accounts";
        return new DescriptionTemplate(description, acceptedTemplate, rejectedTemplate);
    }

    private static DescriptionTemplate mapRegisteredEmailAddressTemplates(String description) {
        String acceptedTemplate = "filing_processed_email_registered_email_address";
        String rejectedTemplate = "filing_rejected_email";
        return new DescriptionTemplate(description, acceptedTemplate, rejectedTemplate);
    }

    private static DescriptionTemplate mapAcspTemplates(String description) {
        String acceptedTemplate;
        String rejectedTemplate;
        if (description.toUpperCase().contains("UPDATE")) {
            acceptedTemplate = "filing_accepted_update_acsp";
            rejectedTemplate = "filing_rejected_update_acsp";
        } else if (description.toUpperCase().contains("CLOSE")) {
            acceptedTemplate = "filing_accepted_close_acsp";
            rejectedTemplate = ""; // CLOSE ACSP filings do not have a rejected template
        } else {
            acceptedTemplate = "filing_accepted_register_acsp";
            rejectedTemplate = "filing_rejected_register_acsp";
        }
        return new DescriptionTemplate(description, acceptedTemplate, rejectedTemplate);
    }

    private static DescriptionTemplate mapDefaultTemplates(String description) {
        String acceptedTemplate = "filing_processed_email";
        String rejectedTemplate = "filing_rejected_email";
        return new DescriptionTemplate(description, acceptedTemplate, rejectedTemplate);
    }
}
