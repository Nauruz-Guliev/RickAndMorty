package ru.example.gnt.common.utils

import ru.example.gnt.common.cutLastOccurence

object ApiQueryGenerator {

    fun generateCharacterIdsQuery(offset: Int, limit: Int): String {
        var query = offset.toString()
        for (i in offset + 1..offset + limit) {
            query += ",$i"
        }
        return query.cutLastOccurence(",")
    }
}

fun main() {
  //  ApiQueryGenerator.generateCharacterIdsQuery(100, 10)
    println(ApiQueryGenerator.generateCharacterIdsQuery(100, 10))
}
