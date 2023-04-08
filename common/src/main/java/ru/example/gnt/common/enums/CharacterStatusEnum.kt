package ru.example.gnt.common.enums


enum class CharacterStatusEnum(val n: String) {
    ALIVE("alive"),
    DEAD("dead"),
    UNKNOWN("unknown");

    companion object : EnumCompanion<String, CharacterStatusEnum>(CharacterStatusEnum::class.java)
}
