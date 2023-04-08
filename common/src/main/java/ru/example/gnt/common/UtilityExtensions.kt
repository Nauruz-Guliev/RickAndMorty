package ru.example.gnt.common

fun String.cutLastOccurence(textToDrop: String): String {
    return if(this.endsWith(textToDrop)) {
        this.substring(0, this.length - textToDrop.length)
    } else {
        this
    }
}
