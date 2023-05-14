package ru.example.gnt.episodes.presentation.details

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.UiState
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.episodes.EpisodesRouter
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import ru.example.gnt.episodes.domain.usecases.GetEpisodeItemByIdUseCase
import ru.example.gnt.episodes.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class EpisodeDetailsViewModelTest {

    var viewModelSuccessMock: EpisodeDetailsViewModel? = null
    var viewModelErrorMock: EpisodeDetailsViewModel? = null

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        val router = RouterMock()
        viewModelSuccessMock = EpisodeDetailsViewModel(
            FAKE_EPISODE_ID,
            GetEpisodeItemByIdUseCase(EpisodeDetailsRepositorySuccessMock()),
            router
        )
        viewModelErrorMock = EpisodeDetailsViewModel(
            FAKE_EPISODE_ID,
            GetEpisodeItemByIdUseCase(EpisodeDetailsRepositoryErrorMock()),
            router
        )
    }

    @Test
    fun uiState_when_initialized_then_uiState_is_empty() {
        assertEquals(UiState.Empty, viewModelErrorMock?.state?.value)
        assertEquals(UiState.Empty, viewModelSuccessMock?.state?.value)
    }

    @Test
    fun uiState_when_value_request_then_uiState_loading_first() = runTest {
        launch(testDispatcher) {
            viewModelSuccessMock?.loadEpisode()
            viewModelErrorMock?.loadEpisode()

            assertEquals(UiState.Loading, viewModelSuccessMock?.state?.first())
            assertEquals(UiState.Loading, viewModelErrorMock?.state?.first())
        }
    }

    @Test
    fun uiState_when_value_requested_with_success_final_result_state_is_success() = runTest {
        viewModelSuccessMock?.loadEpisode()
        assertEquals(
            viewModelSuccessMock?.state?.value,
            UiState.SuccessRemote(createFakeEpisodeDetailsItem())
        )
    }

    @Test
    fun uiState_when_value_requested_with_error_final_result_state_is_error() {
        viewModelErrorMock?.loadEpisode()
        assertEquals(viewModelErrorMock?.state?.value, UiState.Error(FAKE_EXCEPTION))
    }

    @Test
    fun when_navigating_to_characters_id_is_same() = runTest {
        viewModelSuccessMock?.navigateToCharacterDetails(FAKE_CHARACTER_ID_1)

        launch(testDispatcher) {
            assertEquals(FAKE_CHARACTER_ID_1, NAVIGATION_ID_FLOW.first())
        }
    }


    internal class EpisodeDetailsRepositorySuccessMock : EpisodeDetailsRepository {
        override suspend fun getEpisodeDetailsItemById(id: Int): Flow<Result<EpisodeDetailsItem>> {
            return flowOf(
                Result.success(
                    createFakeEpisodeDetailsItem()
                )
            )
        }
    }


    internal class EpisodeDetailsRepositoryErrorMock() : EpisodeDetailsRepository {
        override suspend fun getEpisodeDetailsItemById(id: Int): Flow<Result<EpisodeDetailsItem>> {
            return flowOf(
                Result.failure(
                    FAKE_EXCEPTION
                )
            )
        }
    }


    internal class RouterMock() : EpisodesRouter {
        private val testDispatcher = UnconfinedTestDispatcher()
        override fun navigateToEpisodeDetails(id: Int?) = runTest {
            launch(testDispatcher) {
                NAVIGATION_ID_FLOW.emit(id!!)
            }
        }

        override fun navigateToCharacterDetails(id: Int) = runTest {
            launch(testDispatcher) {
                NAVIGATION_ID_FLOW.emit(id)
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        viewModelErrorMock = null
        viewModelErrorMock = null
    }


    companion object {
        private const val FAKE_EPISODE_ID = 1

        private val NAVIGATION_ID_FLOW = MutableStateFlow(-1)

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


        private val FAKE_EXCEPTION =
            ApplicationException.DataAccessException(cause = java.lang.RuntimeException())

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
    }
}
