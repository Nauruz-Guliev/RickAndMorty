package ru.example.gnt.common.enums

import ru.example.gnt.common.model.Resource


enum class CharacterStatusEnum(val get: String, val color: Resource.Color) {
    ALIVE("alive", Resource.Color(ru.example.gnt.ui.R.color.green_rm)),
    DEAD("dead", Resource.Color(ru.example.gnt.ui.R.color.red_rm)),
    UNKNOWN("unknown", Resource.Color(ru.example.gnt.ui.R.color.gray_70));
    companion object : EnumCompanion<String, CharacterStatusEnum>(CharacterStatusEnum::class.java)
}
