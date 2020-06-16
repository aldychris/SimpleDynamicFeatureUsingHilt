package com.github.aldychris.dynamicmodule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.aldychris.dynamicmodule.core.network.SomeApi
import com.github.aldychris.dynamicmodule.di.SomeApiService
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SomeApiService
    @Inject
    lateinit var api: SomeApi

    private val manager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (manager.installedModules.contains("dynamicfeature1")) {
            btnDlDynamicFeature1.isEnabled = false
            btnOpenDynamicFeature1.isEnabled = true
        } else {
            btnDlDynamicFeature1.isEnabled = true
            btnOpenDynamicFeature1.isEnabled = false
        }
    }

    fun callApi(view: View) {
        api.getDataList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ res ->
                tvStatus.text = res.data
            }, {
                tvStatus.text = it.toString()
            })
    }

    fun dlDynamicFeature1(view: View) {
        val request = SplitInstallRequest.newBuilder()
            .addModule("dynamicfeature1")
            .build()

        manager.startInstall(request)

    }

    fun openDynamicFeature1(view: View) {
        val intent = Intent()
        intent.setClassName(
            BuildConfig.APPLICATION_ID,
            "com.github.aldychris.dynamicfeature1.DynamicFeature1Activity"
        )
        startActivity(intent)
    }
}