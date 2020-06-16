package com.github.aldychris.dynamicmodule

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MultiModuleApp: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

}