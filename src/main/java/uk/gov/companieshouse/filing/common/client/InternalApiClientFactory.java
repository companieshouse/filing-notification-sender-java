package uk.gov.companieshouse.filing.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.filing.common.logging.DataMapHolder;

@Component
public class InternalApiClientFactory {

    private final String internalApiKey;
    private final String privateApiUrl;
    private final String kafkaApiUrl;

    public InternalApiClientFactory(@Value("${api.key.internal}") String internalApiKey,
            @Value("${api.url.private}") String privateApiUrl,
            @Value("${api.url.kafka}") String kafkaApiUrl) {
        this.internalApiKey = internalApiKey;
        this.privateApiUrl = privateApiUrl;
        this.kafkaApiUrl = kafkaApiUrl;
    }

    public InternalApiClient getPrivateApiHostClient() {
        ApiKeyHttpClient httpClient = new ApiKeyHttpClient(internalApiKey);
        httpClient.setRequestId(DataMapHolder.getRequestId());
        InternalApiClient internalApiClient = new InternalApiClient(httpClient);
        internalApiClient.setBasePath(privateApiUrl);
        return internalApiClient;
    }

    public InternalApiClient getKafkaApiHostClient() {
        ApiKeyHttpClient httpClient = new ApiKeyHttpClient(internalApiKey);
        httpClient.setRequestId(DataMapHolder.getRequestId());
        InternalApiClient internalApiClient = new InternalApiClient(httpClient);
        internalApiClient.setBasePath(kafkaApiUrl);
        return internalApiClient;
    }
}
