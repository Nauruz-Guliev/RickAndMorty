package ru.example.gnt.common.di

import android.content.Context
import androidx.annotation.RestrictTo
import kotlin.properties.Delegates

interface CommonModuleDepsProvider {

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: CommonModuleDeps

    companion object : CommonModuleDeps by CommonModuleDepsStore
}

object CommonModuleDepsStore : CommonModuleDeps {
    override var context: Context by Delegates.notNull()
}
