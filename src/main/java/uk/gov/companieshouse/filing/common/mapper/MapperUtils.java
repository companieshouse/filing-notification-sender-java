package uk.gov.companieshouse.filing.common.mapper;

import java.util.Locale;

public class MapperUtils {

    private MapperUtils() {
    }

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
