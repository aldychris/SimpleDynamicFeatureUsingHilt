package com.github.aldychris.coremodule.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.aldychris.coremodule.core.networks.RxErrorHandlingCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule{

    @RestAdapter
    @Provides
    fun provideRestAdapter(): Retrofit {
        val mapper = ObjectMapper().registerModule(KotlinModule())

        return Retrofit.Builder()
            .baseUrl(Const.baseUrl)
            .client(provideClient())
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()

    }

    private fun provideClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
}