package ru.example.gnt.common.base

open class BaseClass(
    open val id: Int,
    open val name: String
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        return result
    }
}
