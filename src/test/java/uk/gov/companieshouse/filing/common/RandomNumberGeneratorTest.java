package uk.gov.companieshouse.filing.common;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RandomNumberGeneratorTest {

    private final RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator();

    @Test
    void generateRandomFiveDigitNumber() {
        String actual = randomNumberGenerator.fiveDigitNumber();
        assertTrue(Integer.parseInt(actual) < 100000);
        assertEquals(5, actual.length());
    }
}