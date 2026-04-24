package uk.gov.companieshouse.filing.common;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberGenerator {

    private static final Random RANDOM = new Random(); // NOSONAR - not used for security purposes

    public String random() {
        return Integer.toString(RANDOM.nextInt(100000));
    }
}
