package ru.example.gnt.common.enums

import org.jetbrains.annotations.NotNull
import ru.example.gnt.common.utils.extensions.cutLastOccurence

/*
Утилитный класс для нахождения подходящего enum'a
 */
open class EnumCompanion<T, B : Enum<B>>(
    private val clazz: Class<B>
) {
    fun find(@NotNull value: T): B? =
        clazz.enumConstants?.find { enumValue ->
            when (value) {
                is String -> value.trim().equals(enumValue.toString(), ignoreCase = true)
                else -> value == enumValue
            }
        }

   /*@return Возвращает строковое значение полей Enum класса
     */
    override fun toString(): String {
        var stringValue = ""
        val divider = ", "
        kotlin.runCatching {
            clazz.fields.forEach {
                stringValue += (it.toString() + divider)
            }
        }
        return stringValue.cutLastOccurence(divider)
    }
}
