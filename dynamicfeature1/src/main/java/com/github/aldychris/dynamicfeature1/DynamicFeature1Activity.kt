package com.github.aldychris.dynamicfeature1

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.aldychris.dynamicfeature1.di.DaggerFeature1Component
import com.github.aldychris.dynamicfeature1.core.network.SomeApiInFeature1
import com.github.aldychris.dynamicfeature1.di.SomeApiServiceInFeature1
import com.github.aldychris.dynamicmodule.di.DynamicFeature1ModuleDependency
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.EntryPointAccessors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dynamic_feature1.*
import javax.inject.Inject

class DynamicFeature1Activity : AppCompatActivity() {

    @SomeApiServiceInFeature1
    @Inject
    lateinit var api: SomeApiInFeature1

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerFeature1Component.builder()
            .context(this)
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    DynamicFeature1ModuleDependency::class.java
                )
            ).build()
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_feature1)



    }

    fun callApiFromDynamicModule1(view: View) {
        api.getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                tvInfo.text = res.data
            },{
                tvInfo.text = it.toString()
            })
    }
}