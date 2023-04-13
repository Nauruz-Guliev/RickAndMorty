package ru.example.gnt.common.utils

import ru.example.gnt.common.model.filter.CharacterFilterModel

object DbQueryGenerator {

    private const val CHARACTER_TABLE_NAME = "character"
    private const val BASE = "SELECT * FROM "

    fun generateCharacterFilterQuery(characterFilter: CharacterFilterModel): String {
        var query = BASE + CHARACTER_TABLE_NAME

        with(characterFilter) {
            if (name != null) {
                query += " WHERE name LIKE '$name'"
            }
            statusList?.forEach {
                query += " AND status LIKE '${it.get}'"
            }
            if (species != null) {
                query += " AND species LIKE '$species'"
            }
            if (type != null) {
                query += " AND type LIKE '$type'"
            }
            genderList?.forEach {
                query += " AND gender LIKE '${it.n}'"
            }
        }
        return query
    }

}

/*
fun main() {
    println(QueryGenerator().generateCharacterFilterQuery(
        CharacterFilterModel(
            name = "rick",
            statusList = listOf(CharacterStatusEnum.ALIVE, CharacterStatusEnum.UNKNOWN),
            species = "human",
            type = "test",
            genderList = listOf(CharacterGenderEnum.UNKNOWN, CharacterGenderEnum.FEMALE)
        )
    ))
}


 */
