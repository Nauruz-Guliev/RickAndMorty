package ru.example.gnt.common.di.deps

import androidx.annotation.RestrictTo
import kotlin.properties.Delegates

interface CommonModuleDepsProvider  {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: CommonModuleDeps

    companion object : CommonModuleDepsProvider by CommonModuleDepsStore

}

object CommonModuleDepsStore: CommonModuleDepsProvider {
    override var deps: CommonModuleDeps by Delegates.notNull()
}
