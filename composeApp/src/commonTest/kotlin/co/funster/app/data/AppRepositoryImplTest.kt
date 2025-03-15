package co.funster.app.data

import co.funster.app.core.model.ApiEvent
import co.funster.app.core.model.City
import co.funster.app.core.model.DateTime
import co.funster.app.core.model.Dates
import co.funster.app.core.model.EmbeddedEventDetails
import co.funster.app.core.model.EmbeddedEvents
import co.funster.app.core.model.Event
import co.funster.app.core.model.EventsResponse
import co.funster.app.core.model.Image
import co.funster.app.core.model.State
import co.funster.app.core.model.Venue
import co.funster.app.core.util.ApiResponse
import co.funster.app.core.util.Either
import co.funster.app.db.LocalDataSource
import co.funster.app.network.RemoteDataSource
import io.mockative.any
import io.mockative.coEvery
import io.mockative.mock
import io.mockative.of
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppRepositoryImplTest {

    private val remoteDataSource: RemoteDataSource = mock(of<RemoteDataSource>())
    private val localDataSource: LocalDataSource = mock(of<LocalDataSource>())
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initializeRepository() = AppRepositoryImpl(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    @Test
    fun `getEvents returns events from network and caches them when successful`() = runTest {
        // Arrange
        val city = "Chicago"
        val venueName = "Palace"
        val stateCode = "IL"
        val imageUrl = "https://example.com/event/1/image.png"
        val startDate = "2025-03-15"
        val endDate = "2025-03-16"
        val page = 0
        val venue = Venue(
            name = venueName,
            city = City(name = city),
            state = State(name = "Illinois", stateCode = stateCode),
            id = "123"
        )
        val testApiEvent = ApiEvent(
            name = "Test Event",
            type = "event",
            id = "1",
            test = false,
            url = "https://example.com/event/1",
            locale = "en-us",
            dates = Dates(
                start = DateTime(localDate = startDate),
                end = DateTime(localDate = "2025-03-16")
            ),
            images = listOf(Image(url = imageUrl)),
            embedded = EmbeddedEventDetails(venues = listOf(venue))
        )
        val expectedEvent = Event(
            id = "1",
            name = "Test Event",
            city = city,
            startDate = startDate,
            imageUrl = imageUrl,
            endDate = endDate,
            location = "$venueName, $city, $stateCode"
        )
        val response = EventsResponse(
            embedded = EmbeddedEvents(events = listOf(testApiEvent))
        )
        coEvery { remoteDataSource.getEvents(city, page) }.returns(response)
        coEvery { localDataSource.saveEvents(any()) }.returns(Unit)

        // Act
        val repository = initializeRepository()
        val result = repository.getEvents(city, page)

        // Assert
        assertTrue(result is Either.Right)
        val events = (result as Either.Right).value
        assertEquals(1, events.size)
        assertEquals(expectedEvent, events[0])
    }

    @Test
    fun `getEvents falls back to cached events on IOException`() = runTest {
        // Arrange
        val city = "Chicago"
        val page = 0
        val apiEvent = Event(
            id = "1",
            name = "Test Event",
            city = city,
            startDate = "2025-03-15",
            imageUrl = "url",
            endDate = "2025-03-18",
            location = "Test location"
        )
        coEvery { remoteDataSource.getEvents(city, page) }.throws(IOException("No network"))
        coEvery { localDataSource.getEvents(city) }.returns(listOf(apiEvent))

        // Act
        val repository = initializeRepository()
        val result = repository.getEvents(city, page)

        // Assert
        assertTrue(result is Either.Right)
        val events = (result as Either.Right).value
        assertEquals(1, events.size)
        assertEquals(apiEvent, events[0])
    }

    @Test
    fun `getEvents returns error when offline and no cached events`() = runTest {
        // Arrange
        val city = "Chicago"
        val page = 0
        coEvery { remoteDataSource.getEvents(city, page) }.throws(IOException("No network"))
        coEvery { localDataSource.getEvents(city) }.returns(emptyList())

        // Act
        val repository = initializeRepository()
        val result = repository.getEvents(city, page)

        // Assert
        assertTrue(result is Either.Left)
        val error = (result as Either.Left).value
        assertTrue(error is ApiResponse.IOException)
        assertEquals("No internet and no cached events for city: $city", error.message)
    }

    @Test
    fun `getEvents returns error on IllegalArgumentException with empty cache`() = runTest {
        // Arrange
        val city = "Chicago"
        val page = 0
        coEvery { remoteDataSource.getEvents(city, page) }.throws(IllegalArgumentException("Invalid input"))
        coEvery { localDataSource.getEvents(city) }.returns(emptyList())

        // Act
        val repository = initializeRepository()
        val result = repository.getEvents(city, page)

        // Assert
        assertTrue(result is Either.Left)
        val error = (result as Either.Left).value
        assertTrue(error is ApiResponse.IOException)
        assertEquals("No internet and no cached events for city: $city", error.message)
    }

    @Test
    fun `getEvents returns unexpected error on generic exception`() = runTest {
        // Arrange
        val city = "Chicago"
        val page = 0
        val exception = Exception("Unexpected error")
        coEvery { remoteDataSource.getEvents(city, page) }.throws(exception)

        // Act
        val repository = initializeRepository()
        val result = repository.getEvents(city, page)

        // Assert
        assertTrue(result is Either.Left)
        val error = (result as Either.Left).value
        assertTrue(error is ApiResponse.HttpError)
        assertEquals("Unexpected error: ${exception.message}", error.message)
    }
}

// Helper function to convert Event to ApiEvent (simplified for testing)
private fun Event.toApiEvent() = ApiEvent(
    name = name,
    id = id,
    type = "event",
    dates = Dates(start = DateTime(localDate = startDate))
)