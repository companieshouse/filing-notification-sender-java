package uk.gov.companieshouse.filing.processed.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.mapper.MapperUtils;

import static uk.gov.companieshouse.filing.common.mapper.Constants.DISSOLUTION_DESCRIPTION;
import static uk.gov.companieshouse.filing.common.mapper.Constants.DISSOLUTION_KIND_DS01;
import static uk.gov.companieshouse.filing.common.mapper.Constants.DISSOLUTION_KIND_LLDS01;

@Component
public class FilingProcessedTemplateMapper {

    private static final String FILING_PROCESSED_TEMPLATE_PREFIX = "filing_processed_email";
    private static final String FILING_REJECTED_TEMPLATE_PREFIX = "filing_rejected_email";

    DescriptionTemplate mapDescriptionTemplates(String description, Transaction transaction) {
        return switch (description) {
            case String desc when desc.contains("insolvency") -> mapInsolvencyDescriptionTemplates(description);
            case String desc when desc.contains("Package accounts") -> mapPackageAccountsTemplates(description);
            case String desc when desc.contains("Registered Email Address") -> mapRegisteredEmailAddressTemplates(description);
            case String desc when desc.toUpperCase().contains("ACSP APPLICATION") -> mapAcspTemplates(description);
            case String desc when desc.contains(DISSOLUTION_DESCRIPTION) -> mapDissolutionTemplates(description, transaction);
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
        if (description.contains("oversea")) {
            acceptedTemplate = "filing_accepted_package_accounts_oversea";
            rejectedTemplate = "filing_rejected_package_accounts_oversea";
        }
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

    private static DescriptionTemplate mapDissolutionTemplates(String description, Transaction transaction) {
        final String templateSuffix = MapperUtils.containsIgnoreCase(transaction.getKind(), DISSOLUTION_KIND_LLDS01)
                ? DISSOLUTION_KIND_LLDS01
                : DISSOLUTION_KIND_DS01;

        final String acceptedTemplate = MapperUtils.constructTemplateName(FILING_PROCESSED_TEMPLATE_PREFIX, templateSuffix);
        final String rejectedTemplate = MapperUtils.constructTemplateName(FILING_REJECTED_TEMPLATE_PREFIX, templateSuffix);
        return new DescriptionTemplate(description, acceptedTemplate, rejectedTemplate);
    }

    private static DescriptionTemplate mapDefaultTemplates(String description) {
        return new DescriptionTemplate(description, FILING_PROCESSED_TEMPLATE_PREFIX, FILING_REJECTED_TEMPLATE_PREFIX);
    }
}
