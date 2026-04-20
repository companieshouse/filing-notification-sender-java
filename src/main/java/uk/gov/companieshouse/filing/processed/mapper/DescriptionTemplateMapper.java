package uk.gov.companieshouse.filing.processed.mapper;

import org.springframework.stereotype.Component;

@Component
public class DescriptionTemplateMapper {

    DescriptionTemplate mapDescriptionTemplate(String description) {
        String acceptedTemplate;
        String rejectedTemplate = "";
        String mappedDescription = description;

        switch (description) {
            case String desc when desc.contains("insolvency") -> {
                acceptedTemplate = "filing_accepted_email_insolvency";
                rejectedTemplate = "filing_rejected_email_insolvency";

                mappedDescription = switch (description) {
                    case String d when d.contains("600") ->
                            "Notice of appointment of liquidator in a creditors' voluntary winding up (600)";
                    case String d when d.contains("LRESEX") -> "Extraordinary resolution to wind up (LRESEX)";
                    case String d when d.contains("LIQ02") -> "Notice of Statement of Affairs (LIQ02)";
                    case String d when d.contains("LIQ03") -> "Notice of Progress Report (LIQ03)";
                    default -> description;
                };
            }
            case String desc when desc.contains("Package accounts") -> {
                acceptedTemplate = "filing_accepted_package_accounts";
                rejectedTemplate = "filing_rejected_package_accounts";
            }
            case String desc when desc.contains("Registered Email Address") -> {
                acceptedTemplate = "filing_processed_email_registered_email_address";
                rejectedTemplate = "filing_rejected_email";
            }
            case String desc when desc.toUpperCase().contains("ACSP APPLICATION") -> {
                if (desc.toUpperCase().contains("UPDATE")) {
                    acceptedTemplate = "filing_accepted_update_acsp";
                    rejectedTemplate = "filing_rejected_update_acsp";
                } else if (desc.toUpperCase().contains("CLOSE")) {
                    acceptedTemplate = "filing_accepted_close_acsp";
                } else {
                    acceptedTemplate = "filing_accepted_register_acsp";
                    rejectedTemplate = "filing_rejected_register_acsp";
                }
            }
            default -> {
                acceptedTemplate = "filing_processed_email";
                rejectedTemplate = "filing_rejected_email";
            }
        }
        return new DescriptionTemplate(mappedDescription, acceptedTemplate, rejectedTemplate);
    }
}
