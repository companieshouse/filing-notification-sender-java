package uk.gov.companieshouse.filing.processed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.common.Service;

@Component
public class FilingProcessedService implements Service<FilingProcessed> {

    @Override
    public void handlePayload(FilingProcessed payload) {
        // handle payload
    }
}
