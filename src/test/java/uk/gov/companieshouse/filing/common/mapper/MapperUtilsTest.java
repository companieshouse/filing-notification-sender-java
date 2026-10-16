package uk.gov.companieshouse.filing.common.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperUtilsTest {

    @Test
    void constructTemplateNameConcatenatesPrefixAndSuffix() {
        assertEquals("filing_received_email_ds01", MapperUtils.constructTemplateName("filing_received_email", "ds01"));
    }

    @Test
    void containsIgnoreCaseReturnsTrueWhenMatchExists() {
        assertTrue(MapperUtils.containsIgnoreCase("dissolution#ds01", "DS01"));
    }

    @Test
    void containsIgnoreCaseReturnsFalseWhenMatchDoesNotExist() {
        assertFalse(MapperUtils.containsIgnoreCase("dissolution#ds01", "in01"));
    }

    @Test
    void containsIgnoreCaseReturnsFalseWhenSourceIsNull() {
        assertFalse(MapperUtils.containsIgnoreCase(null, "value"));
    }

    @Test
    void containsIgnoreCaseThrowsWhenSearchTermIsNull() {
        assertThrows(NullPointerException.class, () -> MapperUtils.containsIgnoreCase("value", null));
    }

    @Test
    void normalizeLowerCasesValue() {
        assertEquals("mixed case", MapperUtils.normalize("MiXeD CaSe"));
    }

    @Test
    void normalizeReturnsEmptyStringForNull() {
        assertEquals("", MapperUtils.normalize(null));
    }
}