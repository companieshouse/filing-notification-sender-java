package uk.gov.companieshouse.filing.received;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.Service;

@Component
public class FilingReceivedService implements Service<FilingReceived> {

    @Override
    public void handlePayload(FilingReceived payload) {
        // handle payload
    }
}
