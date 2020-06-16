package com.github.aldychris.dynamicfeature1.di

import com.github.aldychris.coremodule.di.RestAdapter
import com.github.aldychris.dynamicfeature1.core.network.SomeApiInFeature1
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import retrofit2.Retrofit

@Module
@InstallIn(FragmentComponent::class)
class Feature1Module {

    @SomeApiServiceInFeature1
    @Provides
    fun provideApiService(@RestAdapter restAdapter: Retrofit): SomeApiInFeature1 {
        return restAdapter.create(SomeApiInFeature1::class.java)
    }

}