package uk.gov.companieshouse.filing.common.mapper;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MapperUtils {
    public static String constructTemplateName(String prefix, String suffix) {
        return "%s_%s".formatted(prefix, suffix);
    }

    public static boolean containsIgnoreCase(String source, String searchTerm) {
        return normalize(source).contains(searchTerm.toLowerCase(Locale.ROOT));
    }

    public static String normalize(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}
