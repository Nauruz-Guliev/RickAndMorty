package ru.example.gnt.common.utils

import ru.example.gnt.common.cutLastOccurence
import javax.inject.Inject

class ApiListQueryGenerator @Inject constructor() {

    /**
     * @param list List of ids listOf(5,6,7) will be turned into:
     *  5,6,7
     */
    fun generate(list: List<String>): String {
        var query = ""
        if (list.isEmpty()) return query
        query += list[0]
        list.forEachIndexed { index, id ->
            if (index != 0) {
                query += ",$id"
            }
        }
        return query.cutLastOccurence(",")
    }

}


