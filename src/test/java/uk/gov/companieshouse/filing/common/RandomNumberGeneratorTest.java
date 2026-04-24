package uk.gov.companieshouse.filing.common;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RandomNumberGeneratorTest {

    private final RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator();

    @Test
    void generateRandomNumber() {
        String actual = randomNumberGenerator.random();
        assertTrue(Integer.parseInt(actual) >= 0);
        assertTrue(Integer.parseInt(actual) < 100000);
    }
}