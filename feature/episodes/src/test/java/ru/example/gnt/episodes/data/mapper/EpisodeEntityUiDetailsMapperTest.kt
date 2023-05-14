package ru.example.gnt.episodes.data.mapper

import junit.framework.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem

class EpisodeEntityUiDetailsMapperTest {


    @Test
    fun test_transform_episode_details_to_episode_entity() {
        //arrange
        val mapper = EpisodeEntityUiDetailsMapper()
        val entity = createFakeEpisodeEntity()

        //act
        val episode = mapper.mapTo(entity)

        //assert
        assertTrue(episode is EpisodeDetailsItem)
        assertEquals(episode.episode, FAKE_EPISODE)
        assertEquals(episode.name, FAKE_EPISODE_NAME)
        assertEquals(episode.airDate, FAKE_AIR_DATE)
        assertEquals(episode.created, FAKE_CREATED_DATE)
        assertEquals(episode.url, FAKE_URL)
        // null так как сам маппер не может найти персонажей по id
        assertEquals(episode.characters, null)
    }


    @Test
    fun test_transform_episode_entity_to_episode_details() {
        //arrange
        val mapper = EpisodeEntityUiDetailsMapper()
        val details = createFakeEpisodeDetailsItem()

        //act
        val entity = mapper.mapFrom(details)

        //assert
        assertTrue(entity is EpisodeEntity)

        assertEquals(entity.episode, FAKE_EPISODE)
        assertEquals(entity.name, FAKE_EPISODE_NAME)
        assertEquals(entity.airDate, FAKE_AIR_DATE)
        assertEquals(entity.created, FAKE_CREATED_DATE)
        assertEquals(entity.url, FAKE_URL)
        assertEquals(entity.characters, FAKE_CHARACTER_IDS)
    }




    private fun createFakeEpisodeEntity(): EpisodeEntity {
        return EpisodeEntity(
            id = FAKE_EPISODE_ID,
            airDate = FAKE_AIR_DATE,
            characters = FAKE_CHARACTER_IDS,
            created = FAKE_CREATED_DATE,
            episode = FAKE_EPISODE,
            name = FAKE_EPISODE_NAME,
            url = FAKE_URL
        )
    }


    private fun createFakeEpisodeDetailsItem(): EpisodeDetailsItem {
        return EpisodeDetailsItem(
            id = FAKE_EPISODE_ID,
            name = FAKE_EPISODE_NAME,
            airDate = FAKE_AIR_DATE,
            episode = FAKE_EPISODE,
            characters = createFakeCharacterListItems(),
            url = FAKE_URL,
            created = FAKE_CREATED_DATE
        )
    }

    private fun createFakeCharacterListItems(): List<CharacterListItem> {
        return listOf(
            CharacterListItem(
                id = FAKE_CHARACTER_ID_1,
                name = FAKE_CHARACTER_NAME,
                species = FAKE_SPECIES,
                gender = FAKE_GENDER,
                status = FAKE_STATUS,
                image = FAKE_IMAGE_URL_1,
            ),
            CharacterListItem(
                id = FAKE_CHARACTER_ID_2,
                name = FAKE_CHARACTER_NAME,
                species = FAKE_SPECIES,
                gender = FAKE_GENDER,
                status = FAKE_STATUS,
                image = FAKE_IMAGE_URL_2,
            )
        )
    }

    companion object {
        private const val FAKE_EPISODE_ID = 1
        private const val FAKE_EPISODE_NAME = "NAME"
        private const val FAKE_AIR_DATE = "3, april, 2020"
        private const val FAKE_EPISODE = "episode"
        private const val FAKE_URL = "FAKE_URL"
        private const val FAKE_CREATED_DATE = "2017-11-10T12:56:33.798Z"

        private const val FAKE_CHARACTER_NAME = "character"
        private const val FAKE_SPECIES = "species"
        private val FAKE_GENDER = CharacterGenderEnum.MALE
        private val FAKE_STATUS = CharacterStatusEnum.ALIVE
        private val FAKE_IMAGE_URL_1 = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        private val FAKE_IMAGE_URL_2 = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        private val FAKE_CHARACTER_ID_1 = 1
        private val FAKE_CHARACTER_ID_2 = 2

        private val FAKE_CHARACTER_IDS =
            listOf(FAKE_CHARACTER_ID_1.toString(), FAKE_CHARACTER_ID_2.toString())

    }
}
