package uk.gov.companieshouse.filing.common.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.filing.common.exception.NonRetryableException;
import uk.gov.companieshouse.filing.common.exception.RetryableException;

@ExtendWith(MockitoExtension.class)
class TransactionsApiClientTest {

    private static final String TRANSACTION_ID = "transaction-id";
    private static final String RESOURCE_URI = "/private/transactions/%s".formatted(TRANSACTION_ID);

    @InjectMocks
    private TransactionsApiClient transactionsApiClient;
    @Mock
    private InternalApiClientFactory internalApiClientFactory;

    @Mock
    private InternalApiClient internalApiClient;
    @Mock
    private PrivateTransactionResourceHandler privateTransactionResourceHandler;
    @Mock
    private PrivateTransactionGet privateTransactionGet;

    @Test
    void shouldGetTransactionSuccessfully() throws ApiErrorResponseException, URIValidationException {
        // Given
        Transaction expected = new Transaction();

        stubCommonClientMethods();
        when(privateTransactionGet.execute()).thenReturn(new ApiResponse<>(200, null, expected));

        // When
        Transaction actual = transactionsApiClient.getTransaction(TRANSACTION_ID);

        // Then
        assertEquals(expected, actual);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenApiErrorResponse400() throws ApiErrorResponseException, URIValidationException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(400);

        // when
        Executable executable = () -> transactionsApiClient.getTransaction(TRANSACTION_ID);

        // then
        assertThrows(NonRetryableException.class, executable);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenApiErrorResponse409() throws ApiErrorResponseException, URIValidationException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(409);

        // when
        Executable executable = () -> transactionsApiClient.getTransaction(TRANSACTION_ID);

        // then
        assertThrows(NonRetryableException.class, executable);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowRetryableExceptionWhenApiErrorResponseRetryable() throws ApiErrorResponseException, URIValidationException {
        // given
        stubCommonClientMethods();
        stubErrorResponseException(503);

        // when
        Executable executable = () -> transactionsApiClient.getTransaction(TRANSACTION_ID);

        // then
        assertThrows(RetryableException.class, executable);
        verifyCommonClientMethods();
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenUriValidationException() throws ApiErrorResponseException, URIValidationException {
        // given
        stubCommonClientMethods();
        when(privateTransactionGet.execute()).thenThrow(new URIValidationException("Invalid URI"));

        // when
        Executable executable = () -> transactionsApiClient.getTransaction(TRANSACTION_ID);

        // then
        assertThrows(NonRetryableException.class, executable);
        verifyCommonClientMethods();
    }

    private void stubCommonClientMethods() {
        when(internalApiClientFactory.getPrivateApiHostClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.get(any())).thenReturn(privateTransactionGet);
    }

    private void stubErrorResponseException(int statusCode) throws URIValidationException, ApiErrorResponseException {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(statusCode, "error", new HttpHeaders());
        ApiErrorResponseException errorResponseException = new ApiErrorResponseException(builder);
        when(privateTransactionGet.execute()).thenThrow(errorResponseException);
    }

    private void verifyCommonClientMethods() throws URIValidationException, ApiErrorResponseException {
        verify(internalApiClientFactory).getPrivateApiHostClient();
        verify(internalApiClient).privateTransaction();
        verify(privateTransactionResourceHandler).get(RESOURCE_URI);
        verify(privateTransactionGet).execute();
    }
}
