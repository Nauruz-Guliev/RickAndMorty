package ru.example.gnt.common.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.remote.model.Characters
import ru.example.gnt.common.model.ui.characters.CharactersUiModel

object CharactersDtoUiMapper : BaseMapper<Characters, CharactersUiModel> {
    override fun mapTo(model: Characters): CharactersUiModel {
        return CharactersUiModel(
            singles = model.results?.map { character ->
                CharacterDtoUiMapper.mapTo(character)
            }
        )
    }

    override fun mapFrom(model: CharactersUiModel): Characters {
        return Characters(
            results = model.singles?.map { character ->
                CharacterDtoUiMapper.mapFrom(character)
            },
            info = null
        )
    }
}
