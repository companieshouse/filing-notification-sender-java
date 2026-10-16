package uk.gov.companieshouse.filing.received.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.mapper.MapperUtils;
import uk.gov.companieshouse.filing.received.Transaction;

import static uk.gov.companieshouse.filing.common.Constants.DISSOLUTION_DESCRIPTION;
import static uk.gov.companieshouse.filing.common.Constants.DISSOLUTION_KIND_DS01;
import static uk.gov.companieshouse.filing.common.Constants.DISSOLUTION_KIND_LLDS01;
import static uk.gov.companieshouse.filing.common.Constants.FILING_RECEIVED_TEMPLATE_PREFIX;

@Component
public class FilingReceivedTemplateMapper {

    DescriptionTemplate mapDescriptionTemplates(String description, Transaction item) {
        return switch (description) {
            case String desc when desc.contains("insolvency") -> mapInsolvencyDescriptionTemplates(description, item);
            case String desc when desc.contains("Package accounts") -> mapPackageAccountsTemplates(description);
            case String desc when desc.toUpperCase().contains("ACSP APPLICATION") -> mapAcspTemplates(description);
            case String desc when desc.contains("Dissolution application") -> mapDissolutionTemplates(item);
            default -> mapDefaultTemplates(description);
        };
    }

    private static DescriptionTemplate mapInsolvencyDescriptionTemplates(String description, Transaction item) {
        String template = "filing_received_email_insolvency";

        String mappedDescription = switch (item.getKind()) {
            case String kind when kind.contains("600") ->
                    "Notice of appointment of liquidator in a creditors' voluntary winding up (600)";
            case String kind when kind.contains("LRESEX") -> "Extraordinary resolution to wind up (LRESEX)";
            case String kind when kind.contains("LIQ02") -> "Notice of Statement of Affairs (LIQ02)";
            case String kind when kind.contains("LIQ03") -> "Notice of Progress Report (LIQ03)";
            default -> description;
        };

        return new DescriptionTemplate(mappedDescription, template);
    }

    private static DescriptionTemplate mapPackageAccountsTemplates(String description) {
        String template;
        if (description.contains("oversea")) {
            template = "filing_received_email_package_accounts_oversea";
        } else {
            template = "filing_received_email_package_accounts";
        }

        return new DescriptionTemplate(description, template);
    }

    private static DescriptionTemplate mapAcspTemplates(String description) {
        String template;
        if (description.toUpperCase().contains("UPDATE")) {
            template = "filing_received_email_update_acsp";
        } else if (description.toUpperCase().contains("CLOSE")) {
            template = "filing_received_email_close_acsp";
        } else {
            template = "filing_received_email_acsp_registration";
        }
        return new DescriptionTemplate(description, template);
    }

    private static DescriptionTemplate mapDissolutionTemplates(Transaction item) {
        String templateSuffix = MapperUtils.containsIgnoreCase(item.getKind(), DISSOLUTION_KIND_LLDS01)
                ? DISSOLUTION_KIND_LLDS01
                : DISSOLUTION_KIND_DS01;

        return new DescriptionTemplate(DISSOLUTION_DESCRIPTION, MapperUtils.constructTemplateName(FILING_RECEIVED_TEMPLATE_PREFIX, templateSuffix));
    }

    private static DescriptionTemplate mapDefaultTemplates(String description) {
        return new DescriptionTemplate(description, FILING_RECEIVED_TEMPLATE_PREFIX);
    }
}
