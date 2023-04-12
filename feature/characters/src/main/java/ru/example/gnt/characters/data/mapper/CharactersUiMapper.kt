package ru.example.gnt.characters.data.mapper

import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.Characters

internal object CharactersUiMapper : BaseMapper<Characters, CharactersUiModel> {
    override fun mapTo(model: Characters): CharactersUiModel {
        return CharactersUiModel(
            info = CharactersUiModel.Info(
                count = model.info?.count,
                next = model.info?.next,
                pages = model.info?.pages,
                prev = model.info?.prev
            ),
            singles = model.results?.map { character ->
                CharacterSingleUIMapper.mapTo(character)
            }
        )
    }

    override fun mapFrom(model: CharactersUiModel): Characters {
        return Characters(
            info = Characters.Info(
                count = model.info?.count,
                next = model.info?.next,
                pages = model.info?.pages,
                prev = model.info?.prev
            ),
            results = model.singles?.map { character ->
                CharacterSingleUIMapper.mapFrom(character)
            }
        )
    }
}
