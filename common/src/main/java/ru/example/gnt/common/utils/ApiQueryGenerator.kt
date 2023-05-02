package ru.example.gnt.common.utils

import ru.example.gnt.common.cutLastOccurence

class ApiListQueryGenerator {

    /**
     * @param list List of ids will be turned into
     * example listOf(5,6,7)
     * will turn into:
     * 5,6,7
     */
    fun generate(list: List<String>): String {
        var query = ""
        if (list.isEmpty()) return query
        query += list[0]
        list.forEach { id ->
            query += ",$id"
        }
        return query.cutLastOccurence(",")
    }

}


