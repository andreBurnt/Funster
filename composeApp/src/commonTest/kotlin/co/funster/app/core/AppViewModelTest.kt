package co.funster.app.core

import co.funster.app.core.model.Event
import co.funster.app.core.util.Either
import co.funster.app.domain.GetEventsUseCase
import io.mockative.any
import io.mockative.coEvery
import io.mockative.mock
import io.mockative.of
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val getEventsUseCase: GetEventsUseCase = mock(of<GetEventsUseCase>())
    private val settings: SettingsWrapper = mock(of<SettingsWrapper>())
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initializeViewModel() = AppViewModel(
        getEventsUseCase = getEventsUseCase,
        settings = settings
    )

    @Test
    fun `init loads events and sets initial state`() = runTest {
        // Arrange
        val events = listOf(
            Event(
                id = "1",
                name = "Event 1",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(events))

        // Act
        val viewModel = initializeViewModel()

        // Assert
        assertEquals(HomePageUiState.EventsLoaded(events), viewModel.uiState.first())
        assertFalse(viewModel.isRefreshing.first())
    }

    @Test
    fun `refreshEvents loads events and updates state`() = runTest {
        // Arrange
        val events = listOf(
            Event(
                id = "1",
                name = "Event 1",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(events))
        val viewModel = initializeViewModel()

        // Act
        viewModel.refreshEvents()

        // Assert
        assertEquals(HomePageUiState.EventsLoaded(events), viewModel.uiState.first())
        assertFalse(viewModel.isRefreshing.first())
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun `refreshEvents sets empty state when no events`() = runTest {
        // Arrange
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Left("No events found"))
        val viewModel = initializeViewModel()

        // Act
        viewModel.refreshEvents()

        // Assert
        assertEquals(HomePageUiState.Empty, viewModel.uiState.first())
        assertFalse(viewModel.isRefreshing.first())
        assertEquals("Failed to load events: No events found", viewModel.errorMessage.first())
    }

    @Test
    fun `loadMoreEvents appends events to existing list`() = runTest {
        // Arrange
        val initialEvents = listOf(
            Event(
                id = "1",
                name = "Event 1",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            )
        )
        val moreEvents = listOf(
            Event(
                id = "2",
                name = "Event 2",
                imageUrl = "https://example.com/image2.jpg",
                startDate = "2025-03-16",
                endDate = null,
                city = "Chicago",
                location = "Venue 2, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(initialEvents))
        coEvery { getEventsUseCase.invoke("Chicago", 1) }.returns(Either.Right(moreEvents))
        val viewModel = initializeViewModel()

        // Act
        viewModel.loadMoreEvents()

        // Assert
        val state = viewModel.uiState.first() as HomePageUiState.EventsLoaded
        assertEquals(initialEvents + moreEvents, state.events)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun `setSearchQuery filters events by name`() = runTest {
        // Arrange
        val events = listOf(
            Event(
                id = "1",
                name = "Concert A",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            ),
            Event(
                id = "2",
                name = "Theater B",
                imageUrl = "https://example.com/image2.jpg",
                startDate = "2025-03-16",
                endDate = null,
                city = "Chicago",
                location = "Venue 2, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(events))
        val viewModel = initializeViewModel()

        // Act
        viewModel.setSearchQuery("Concert")

        // Assert
        val state = viewModel.uiState.first() as HomePageUiState.EventsLoaded
        assertEquals(listOf(events[0]), state.events)
    }

    @Test
    fun `setSearchQuery filters events by location`() = runTest {
        // Arrange
        val events = listOf(
            Event(
                id = "1",
                name = "Concert A",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Downtown, Chicago, IL"
            ),
            Event(
                id = "2",
                name = "Theater B",
                imageUrl = "https://example.com/image2.jpg",
                startDate = "2025-03-16",
                endDate = null,
                city = "Chicago",
                location = "Uptown, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(events))
        val viewModel = initializeViewModel()

        // Act
        viewModel.setSearchQuery("Downtown")

        // Assert
        val state = viewModel.uiState.first() as HomePageUiState.EventsLoaded
        assertEquals(listOf(events[0]), state.events)
    }

    @Test
    fun `setCity refreshes events for new city`() = runTest {
        // Arrange
        val chicagoEvents = listOf(
            Event(
                id = "1",
                name = "Event 1",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            )
        )
        val newYorkEvents = listOf(
            Event(
                id = "2",
                name = "Event 2",
                imageUrl = "https://example.com/image2.jpg",
                startDate = "2025-03-16",
                endDate = null,
                city = "New York",
                location = "Venue 2, New York, NY"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(chicagoEvents))
        coEvery { getEventsUseCase.invoke("New York", 0) }.returns(Either.Right(newYorkEvents))
        val viewModel = initializeViewModel()

        // Act
        viewModel.setCity("New York")

        // Assert
        assertEquals(HomePageUiState.EventsLoaded(newYorkEvents), viewModel.uiState.first())
        assertEquals("New York", viewModel.selectedCity.first())
    }

    @Test
    fun `dismissError clears error message`() = runTest {
        // Arrange
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Left("Network error"))
        val viewModel = initializeViewModel()

        // Act
        viewModel.refreshEvents() // Sets error message
        viewModel.dismissError()

        // Assert
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun `loadEventsForPage keeps existing events on error after loading some`() = runTest {
        // Arrange
        val initialEvents = listOf(
            Event(
                id = "1",
                name = "Event 1",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = null,
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            )
        )
        coEvery { getEventsUseCase.invoke("Chicago", 0) }.returns(Either.Right(initialEvents))
        coEvery { getEventsUseCase.invoke("Chicago", 1) }.returns(Either.Left("Network error"))
        val viewModel = initializeViewModel()

        // Act
        viewModel.loadMoreEvents()

        // Assert
        val state = viewModel.uiState.first() as HomePageUiState.EventsLoaded
        assertEquals(initialEvents, state.events)
        assertFalse(state.isLoadingMore)
        assertEquals("Failed to load events: Network error", viewModel.errorMessage.first())
    }
}
