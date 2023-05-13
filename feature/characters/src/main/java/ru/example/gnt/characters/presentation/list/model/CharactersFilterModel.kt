package ru.example.gnt.characters.presentation.list.model

import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

class CharactersFilterModel private constructor(
    var status: CharacterStatusEnum? = null,
    var gender: CharacterGenderEnum? = null,
    var species: String? = null,
    var name: String? = null,
    var type: String? = null,
) {
    companion object {
        fun builder(): Builder = Builder()
    }
    class Builder {
        private var status: CharacterStatusEnum? = null
        private var gender: CharacterGenderEnum? = null
        private var species: String? = null
        private var name: String? = null
        private var type: String? = null

        fun status(status: CharacterStatusEnum?) = apply { this.status = status }
        fun gender(gender: CharacterGenderEnum?) = apply { this.gender = gender }
        fun species(species: String?) = apply { this.species = species }
        fun name(name: String?) = apply { this.name = name }
        fun type(type: String?) = apply { this.type = type }
        fun build() = CharactersFilterModel(
            status = status, gender = gender, species = species, name = name, type = type
        )
    }
}


