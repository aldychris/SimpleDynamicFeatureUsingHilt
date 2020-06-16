package com.github.aldychris.dynamicmodule.di

import com.github.aldychris.coremodule.di.RestAdapter
import com.github.aldychris.dynamicmodule.core.network.SomeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

    @SomeApiService
    @Provides
    fun provideApiService(@RestAdapter restAdapter: Retrofit): SomeApi {
        return restAdapter.create(SomeApi::class.java)
    }

}