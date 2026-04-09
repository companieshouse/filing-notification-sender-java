package uk.gov.companieshouse.filing.common.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateMessageSendHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateMessageSendPost;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.common.exception.RetryableException;

@ExtendWith(MockitoExtension.class)
class KafkaApiClientTest {

    private static final String RESOURCE_URI = "/message-send";

    @InjectMocks
    private KafkaApiClient kafkaApiClient;
    @Mock
    private InternalApiClientFactory internalApiClientFactory;

    @Mock
    private MessageSend messageSend;
    @Mock
    private InternalApiClient internalApiClient;
    @Mock
    private PrivateMessageSendHandler privateMessageSendHandler;
    @Mock
    private PrivateMessageSendPost privateMessageSendPost;

    @Test
    void shouldPostMessageSendSuccessfully() throws ApiErrorResponseException {
        // Given
        stubCommonClientMethods();
        when(privateMessageSendPost.execute()).thenReturn(new ApiResponse<>(200, null, null));

        // When
        kafkaApiClient.postMessageSend(messageSend);

        // Then
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenApiErrorResponse400() throws ApiErrorResponseException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(400);

        // when
        Executable executable = () -> kafkaApiClient.postMessageSend(messageSend);

        // then
        assertThrows(NonRetryableException.class, executable);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenApiErrorResponse409() throws ApiErrorResponseException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(409);

        // when
        Executable executable = () -> kafkaApiClient.postMessageSend(messageSend);

        // then
        assertThrows(NonRetryableException.class, executable);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowRetryableExceptionWhenApiErrorResponseRetryable() throws ApiErrorResponseException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(503);

        // when
        Executable executable = () -> kafkaApiClient.postMessageSend(messageSend);

        // then
        assertThrows(RetryableException.class, executable);
        verifyCommonClientMethods();
    }

    private void stubCommonClientMethods() {
        when(internalApiClientFactory.getKafkaApiHostClient()).thenReturn(internalApiClient);
        when(internalApiClient.messageSendHandler()).thenReturn(privateMessageSendHandler);
        when(privateMessageSendHandler.postMessageSend(any(), any())).thenReturn(privateMessageSendPost);
    }

    private void stubErrorResponseException(int statusCode) throws ApiErrorResponseException {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(statusCode, "error", new HttpHeaders());
        ApiErrorResponseException errorResponseException = new ApiErrorResponseException(builder);
        when(privateMessageSendPost.execute()).thenThrow(errorResponseException);
    }

    private void verifyCommonClientMethods() throws ApiErrorResponseException {
        verify(internalApiClientFactory).getKafkaApiHostClient();
        verify(internalApiClient).messageSendHandler();
        verify(privateMessageSendHandler).postMessageSend(RESOURCE_URI, messageSend);
        verify(privateMessageSendPost).execute();
    }
}
