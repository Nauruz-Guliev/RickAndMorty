package ru.example.gnt.common.base

interface BaseMapper<From, To> {
    fun mapTo(model: From): To
    fun mapFrom(model: To): From
}
