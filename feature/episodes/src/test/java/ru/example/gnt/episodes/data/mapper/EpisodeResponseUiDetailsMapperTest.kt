package ru.example.gnt.episodes.data.mapper

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem

class EpisodeResponseUiDetailsMapperTest {

    @Test
    fun test_transform_episode_response_to_episode_details() {
        // arrange
        val urlIdExtractor = UrlIdExtractor()
        val mapper = EpisodeResponseUiDetailsMapper(
            urlIdExtractor = urlIdExtractor,
            baseUrl = "https://rickandmortyapi.com/api/"
        )

        val response = createFakeEpisodeResponseResultModel()

        // act
        val episodeDetails = mapper.mapTo(response)

        // assert
        assertTrue(episodeDetails is EpisodeDetailsItem)
        assertEquals(episodeDetails.episode, FAKE_EPISODE)
        assertEquals(episodeDetails.id, FAKE_EPISODE_ID)
        assertEquals(episodeDetails.name, FAKE_EPISODE_NAME)
        assertEquals(episodeDetails.url, FAKE_URL)
        assertEquals(episodeDetails.airDate, FAKE_AIR_DATE)
        assertEquals(episodeDetails.created, FAKE_CREATED_DATE)
        // маппер самостоятельно не может найти персонажей по id, поэтому null
        assertEquals(episodeDetails.characters, null)
    }

    @Test
    fun test_transform_episode_details_to_episode_entity() {
        // arrange
        val urlIdExtractor = UrlIdExtractor()
        val mapper = EpisodeResponseUiDetailsMapper(
            urlIdExtractor = urlIdExtractor,
            baseUrl = "https://rickandmortyapi.com/api/"
        )

        val response = createFakeEpisodeDetailsItem()

        // act
        val episodeResponse = mapper.mapFrom(response)

        // assert
        assertTrue(episodeResponse is EpisodesResponseModel.Result)
        assertEquals(episodeResponse.episode, FAKE_EPISODE)
        assertEquals(episodeResponse.id, FAKE_EPISODE_ID)
        assertEquals(episodeResponse.name, FAKE_EPISODE_NAME)
        assertEquals(episodeResponse.url, FAKE_URL)
        assertEquals(episodeResponse.airDate, FAKE_AIR_DATE)
        assertEquals(episodeResponse.created, FAKE_CREATED_DATE)
        assertEquals(episodeResponse.characters, FAKE_CHARACTER_IDS)
    }


    private fun createFakeEpisodeResponseResultModel(): EpisodesResponseModel.Result {
        return EpisodesResponseModel.Result(
            airDate = FAKE_AIR_DATE,
            characters = FAKE_CHARACTER_IDS,
            created = FAKE_CREATED_DATE,
            episode = FAKE_EPISODE,
            id = FAKE_EPISODE_ID,
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
        val urlIdExtractor = UrlIdExtractor()
        return listOf(
            CharacterListItem(
                id = Integer.valueOf(urlIdExtractor.extract(FAKE_CHARACTER_ID_1)),
                name = FAKE_CHARACTER_NAME,
                species = FAKE_SPECIES,
                gender = FAKE_GENDER,
                status = FAKE_STATUS,
                image = FAKE_IMAGE_URL_1,
            ),
            CharacterListItem(
                id = Integer.valueOf(urlIdExtractor.extract(FAKE_CHARACTER_ID_2)),
                name = FAKE_CHARACTER_NAME,
                species = FAKE_SPECIES,
                gender = FAKE_GENDER,
                status = FAKE_STATUS,
                image = FAKE_IMAGE_URL_2,
            )
        )
    }


    companion object {
        private const val FAKE_BASE_URL = "https://rickandmortyapi.com/"

        private const val FAKE_EPISODE_LIST_SIZE = 10
        private const val FAKE_EPISODE_NEXT = "https://rickandmortyapi.com/api/character/?page=3"
        private const val FAKE_EPISODE_PREV = "https://rickandmortyapi.com/api/character/?page=1"

        private const val FAKE_PAGES_COUNT = 20

        private const val FAKE_EPISODE_ID = 1
        private const val FAKE_EPISODE_NAME = "NAME"
        private const val FAKE_AIR_DATE = "3, april, 2020"
        private const val FAKE_EPISODE = "episode"
        private const val FAKE_URL = "https://rickandmortyapi.com/api/episode/1"
        private const val FAKE_CREATED_DATE = "2017-11-10T12:56:33.798Z"

        private const val FAKE_CHARACTER_NAME = "character"
        private const val FAKE_SPECIES = "species"
        private val FAKE_GENDER = CharacterGenderEnum.MALE
        private val FAKE_STATUS = CharacterStatusEnum.ALIVE
        private val FAKE_IMAGE_URL_1 = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        private val FAKE_IMAGE_URL_2 = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        private val FAKE_CHARACTER_ID_1 = "https://rickandmortyapi.com/api/character/1"
        private val FAKE_CHARACTER_ID_2 = "https://rickandmortyapi.com/api/character/2"

        private val FAKE_CHARACTER_IDS =
            listOf(FAKE_CHARACTER_ID_1, FAKE_CHARACTER_ID_2)
    }
}
