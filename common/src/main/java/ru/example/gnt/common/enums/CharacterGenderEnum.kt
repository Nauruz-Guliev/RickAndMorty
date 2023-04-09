package ru.example.gnt.common.enums

enum class CharacterGenderEnum(
    val n: String
) {
    FEMALE("female"),
    MALE("male"),
    GENDERLESS("genderless"),
    UNKNOWN("unknown");

    companion object : EnumCompanion<String, CharacterGenderEnum>(CharacterGenderEnum::class.java)
}
