package ru.example.gnt.characters.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.remote.model.CharactersResponseModel

object CharactersDtoUiMapper : BaseMapper<CharactersResponseModel, ru.example.gnt.characters.presentation.list.model.CharactersUiModel> {
    override fun mapTo(model: CharactersResponseModel): ru.example.gnt.characters.presentation.list.model.CharactersUiModel {
        return ru.example.gnt.characters.presentation.list.model.CharactersUiModel(
            singles = model.results?.map { character ->
                CharacterDtoUiMapper.mapTo(character)
            }
        )
    }

    override fun mapFrom(model: ru.example.gnt.characters.presentation.list.model.CharactersUiModel): CharactersResponseModel {
        return CharactersResponseModel(
            results = model.singles?.map { character ->
                CharacterDtoUiMapper.mapFrom(character)
            },
            info = null
        )
    }
}
