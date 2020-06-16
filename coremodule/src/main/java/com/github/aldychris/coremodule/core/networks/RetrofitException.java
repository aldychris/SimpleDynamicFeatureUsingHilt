package com.github.aldychris.coremodule.core.networks;

import java.io.IOException;
import java.lang.annotation.Annotation;

import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitException extends RuntimeException {

    public static ApiException httpError(String url, Response response, Retrofit retrofit) {
        return new ApiException(url, response, retrofit);
    }

    public static NetworkException networkError(IOException exception) {
        return new NetworkException(exception);
    }

    public static UnexpectedException unexpectedError(Throwable exception) {
        return new UnexpectedException(exception);
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED;


    }

    private final String url;
    private final Response response;

    private final Kind kind;
    private final Retrofit retrofit;

    RetrofitException(String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.kind = kind;
        this.retrofit = retrofit;
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    @Nullable
    public Response getResponse() {
        return response;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * The Retrofit this request was executed on
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     * <p>
     * Response.errorBody() will become empty after conversion
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
        T errorBody = converter.convert(response.errorBody());

        return errorBody;
    }

    public static class ApiException extends RetrofitException {

        private String errorMessage;
//        private ErrorResponse errorResponse = null;
        private int responseCode;

        public ApiException(String url, Response response, Retrofit retrofit) {
            super(response.message(), url, response, Kind.HTTP, null, retrofit);

//            try {
                errorMessage = response.message();
//                errorResponse = getErrorBodyAs(ErrorResponse.class);
//
//                if (!TextUtil.isEmpty(errorResponse.getResponseMessage()))
//                    errorMessage = errorResponse.getResponseMessage();
//
//                else if (!TextUtil.isEmpty(errorResponse.getErrorMessage()))
//                    errorMessage = errorResponse.getErrorMessage();

                errorMessage += " (" + response.code() + ")";


//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            this.responseCode = response.code();
        }

//        public ErrorResponse getErrorResponse() throws IOException {
//            return errorResponse;
//        }

        @Override
        public String getMessage() {
            return errorMessage;
        }

        public int getResponseCode() { return responseCode; }
    }

    public static class NetworkException extends RetrofitException {

        public NetworkException(IOException exception) {
            super(exception.getMessage(), null, null, Kind.NETWORK, exception, null);
        }

        @Nullable
        @Override
        public Response getResponse() {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnexpectedException extends RetrofitException {

        public UnexpectedException(Throwable exception) {
            super(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
        }
    }
}
