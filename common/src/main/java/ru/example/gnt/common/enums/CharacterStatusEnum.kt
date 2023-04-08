package ru.example.gnt.common.enums

import androidx.annotation.ColorRes


enum class CharacterStatusEnum(val n: String, @ColorRes val color: Int) {
    ALIVE("alive", ru.example.gnt.ui.R.color.green_rm),
    DEAD("dead", ru.example.gnt.ui.R.color.red_rm),
    UNKNOWN("unknown", ru.example.gnt.ui.R.color.gray_70);
    companion object : EnumCompanion<String, CharacterStatusEnum>(CharacterStatusEnum::class.java)
}
