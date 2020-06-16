package com.github.aldychris.coremodule.core.networks;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJava2CallAdapterFactory original;
    private final ResponseBodyConverter.Factory converterFactory;
    private final CustomCallConstructor customCallConstructor;

    public interface ResponseBodyConverter<T> {

        T convert(Request request, ResponseBody body) throws IOException;

        interface Factory {
            ResponseBodyConverter<?> responseBodyConverter(Type type, Annotation[] annotations,
                                                           Retrofit retrofit);
        }
    }

    public interface CustomCallConstructor {
        Call<ResponseBody> onConstructCustomCall(Call<ResponseBody> originCall, ResponseBodyConverter defaultConverter);
    }

    public static CallAdapter.Factory create() {
        return create(null);
    }

    public static CallAdapter.Factory create(CustomCallConstructor customCall) {
        return new RxErrorHandlingCallAdapterFactory(customCall);
    }

    private RxErrorHandlingCallAdapterFactory(CustomCallConstructor customCallConstructor) {
        this.customCallConstructor = customCallConstructor;
        original = RxJava2CallAdapterFactory.create();
        this.converterFactory = (type, annotations, retrofit) -> (ResponseBodyConverter<Object>) (request, body) -> retrofit.responseBodyConverter(type, annotations).convert(body);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<?, ?> callAdapter = original.get(returnType, annotations, retrofit);

        ResponseBodyConverter<Object> converter = (ResponseBodyConverter<Object>) converterFactory.responseBodyConverter(
                callAdapter.responseType(), annotations, retrofit);

        return new RxCallAdapterWrapper(retrofit, callAdapter, converter);
    }

    private class RxCallAdapterWrapper<R> implements CallAdapter<ResponseBody, Object> {
        private final Retrofit retrofit;
        private final CallAdapter<ResponseBody, Object> wrapped;
        private final ResponseBodyConverter<Object> converter;


        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<ResponseBody, Object> callAdapter, ResponseBodyConverter<Object> converter) {
            this.retrofit = retrofit;
            this.wrapped = callAdapter;
            this.converter = converter;
        }

        @Override
        public Type responseType() {
            return customCallConstructor != null ? ResponseBody.class : wrapped.responseType();
        }

        @Override
        public Object adapt(Call<ResponseBody> call) {

            Object adapt = wrapped.adapt(customCallConstructor != null ? customCallConstructor.onConstructCustomCall(call, converter) : call);

            if (adapt instanceof Observable) {
                return ((Observable) adapt).onErrorResumeNext(throwable -> {
                    return Observable.error(asRetrofitException((Throwable) throwable));
                });
            } else if (adapt instanceof Single) {
                return ((Single) adapt).onErrorResumeNext(throwable -> Single.error(asRetrofitException((Throwable) throwable)));
            } else if (adapt instanceof Completable) {
                return ((Completable) adapt).onErrorResumeNext(throwable -> Completable.error(asRetrofitException(throwable)));
            }

            return null;
        }

        private Throwable asRetrofitException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return RetrofitException.unexpectedError(throwable);
        }
    }
}