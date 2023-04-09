package ru.example.gnt.common.di.viewmodel

import kotlin.reflect.KClass
import androidx.lifecycle.ViewModel
import dagger.MapKey

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
