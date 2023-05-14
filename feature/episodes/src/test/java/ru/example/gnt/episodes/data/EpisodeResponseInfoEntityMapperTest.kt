package ru.example.gnt.episodes.data

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.episodes.data.mapper.EpisodeResponseInfoEntityMapper

class EpisodeResponseInfoEntityMapperTest {

    @Test
    fun test_transform_episode_response_to_episode_entity() {
        //arrange
        val urlIdExtractor = UrlIdExtractor()
        val entityMapper = EpisodeEntityResponseMapper(urlIdExtractor, FAKE_BASE_URL)
        val mapper = EpisodeResponseInfoEntityMapper(entityMapper)
        val responseModel = createFakeEpisodeResponse()

        //act
        val listOfEntities = mapper.mapTo(responseModel)

        //assert
        assertTrue(listOfEntities is List<EpisodeEntity>)

        assertEquals(listOfEntities.size, responseModel.results.size)
        assertEquals(listOfEntities.size, FAKE_EPISODE_LIST_SIZE)
    }

    @Test
    fun test_transform_episode_entity_to_episode_details() {
        //arrange
        val urlIdExtractor = UrlIdExtractor()
        val entityMapper = EpisodeEntityResponseMapper(urlIdExtractor, FAKE_BASE_URL)
        val mapper = EpisodeResponseInfoEntityMapper(entityMapper)
        val listOfEntities = createFakeListOfEpisodeEntities()

        // act
        val responseModel = mapper.mapFrom(listOfEntities)

        // assert
        assertTrue(responseModel is EpisodesResponseModel)
        assertEquals(responseModel.results.size, listOfEntities.size)
        assertEquals(responseModel.results.size, FAKE_EPISODE_LIST_SIZE)

    }

    @Test
    fun test_empty_list_give_returns_result_with_empty_list() {
        // arrange
        val urlIdExtractor = UrlIdExtractor()
        val entityMapper = EpisodeEntityResponseMapper(urlIdExtractor, FAKE_BASE_URL)
        val mapper = EpisodeResponseInfoEntityMapper(entityMapper)
        val listOfEntities = listOf<EpisodeEntity>()
        val response = createFakeEpisodeResponse().apply {
            results = listOf()
        }
        // act
        val transformedEntities = mapper.mapFrom(listOfEntities)
        val transformedResponse = mapper.mapTo(response)

        // assert
        assertEquals(transformedEntities.results.size, 0)
        assertEquals(transformedResponse.size, 0)
    }


    private fun createFakeListOfEpisodeEntities(): List<EpisodeEntity> {
        val list = mutableListOf<EpisodeEntity>()
        for (i in 0 until FAKE_EPISODE_LIST_SIZE) {
            list.add(createFakeEpisodeEntity())
        }
        return list
    }

    private fun createFakeEpisodeResponse(): EpisodesResponseModel {
        return EpisodesResponseModel(
            info = createFakeEpisodeResponseInfoModel(),
            results = createFakeListOfEpisodeResults()
        )
    }

    private fun createFakeEpisodeResponseInfoModel(): EpisodesResponseModel.Info {
        return EpisodesResponseModel.Info(
            count = FAKE_EPISODE_LIST_SIZE,
            next = FAKE_EPISODE_NEXT,
            prev = FAKE_EPISODE_PREV,
            pages = FAKE_PAGES_COUNT
        )
    }

    private fun createFakeListOfEpisodeResults(): List<EpisodesResponseModel.Result> {
        val list = mutableListOf<EpisodesResponseModel.Result>()
        for (i in 0 until FAKE_EPISODE_LIST_SIZE) {
            list.add(createFakeEpisodeResponseResultModel())
        }
        return list
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

        private val FAKE_CHARACTER_ID_1 = "https://rickandmortyapi.com/api/character/1"
        private val FAKE_CHARACTER_ID_2 = "https://rickandmortyapi.com/api/character/2"

        private val FAKE_CHARACTER_IDS =
            listOf(FAKE_CHARACTER_ID_1, FAKE_CHARACTER_ID_2)
    }
}
