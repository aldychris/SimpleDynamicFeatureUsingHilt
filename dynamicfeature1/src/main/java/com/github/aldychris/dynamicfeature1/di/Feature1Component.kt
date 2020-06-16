package com.github.aldychris.dynamicfeature1.di

import android.content.Context
import com.github.aldychris.dynamicfeature1.DynamicFeature1Activity
import com.github.aldychris.dynamicmodule.di.DynamicFeature1ModuleDependency
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [Feature1Module::class],
    dependencies = [DynamicFeature1ModuleDependency::class]
)
interface Feature1Component {

    fun inject(activity: DynamicFeature1Activity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(moduleDependencies: DynamicFeature1ModuleDependency): Builder
        fun moduleFeature1Dependencies(feature1ModuleModule: Feature1Module): Builder
        fun build(): Feature1Component
    }

}
