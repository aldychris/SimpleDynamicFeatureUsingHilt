package com.github.aldychris.dynamicmodule.di

import com.github.aldychris.coremodule.di.RestAdapter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@EntryPoint
@InstallIn(ApplicationComponent::class)
interface DynamicFeature1ModuleDependency {
    @RestAdapter
    fun provideApiAdapter(): Retrofit
}
