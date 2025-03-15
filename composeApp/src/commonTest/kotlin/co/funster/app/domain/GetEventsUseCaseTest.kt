package co.funster.app.domain

import co.funster.app.core.model.Event
import co.funster.app.core.util.ApiResponse
import co.funster.app.core.util.Either
import co.funster.app.data.AppRepository
import io.mockative.coEvery
import io.mockative.mock
import io.mockative.of
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetEventsUseCaseTest {

    private val appRepository: AppRepository = mock(of<AppRepository>())

    private fun initializeUseCase() = GetEventsUseCase(
        appRepository = appRepository
    )

    @Test
    fun `invoke returns events on successful repository call`() = runTest {
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
            )
        )
        coEvery { appRepository.getEvents(city = "Chicago", page = 0) }
            .returns(Either.Right(events))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "Chicago", page = 0)

        // Assert
        assertTrue(result is Either.Right)
        assertEquals(events, (result as Either.Right).value)
    }

    @Test
    fun `invoke returns empty list when repository returns empty success`() = runTest {
        // Arrange
        coEvery { appRepository.getEvents(city = "Chicago", page = 0) }
            .returns(Either.Right(emptyList()))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "Chicago", page = 0)

        // Assert
        assertTrue(result is Either.Right)
        assertEquals(emptyList(), (result as Either.Right).value)
    }

    @Test
    fun `invoke maps IOException to network unavailable message`() = runTest {
        // Arrange
        coEvery { appRepository.getEvents(city = "Chicago", page = 0) }
            .returns(Either.Left(ApiResponse.IOException("Network failure")))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "Chicago", page = 0)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals("Network unavailable", (result as Either.Left).value)
    }

    @Test
    fun `invoke maps HttpError to generic error message`() = runTest {
        // Arrange
        coEvery { appRepository.getEvents(city = "Chicago", page = 0) }
            .returns(Either.Left(ApiResponse.HttpError("404 Not Found")))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "Chicago", page = 0)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals("Error getting events, try again later", (result as Either.Left).value)
    }

    @Test
    fun `invoke passes correct city and page to repository`() = runTest {
        // Arrange
        val events = listOf(
            Event(
                id = "2",
                name = "Theater B",
                imageUrl = "https://example.com/image2.jpg",
                startDate = "2025-03-16",
                endDate = null,
                city = "New York",
                location = "Venue 2, New York, NY"
            )
        )
        coEvery { appRepository.getEvents(city = "New York", page = 1) }
            .returns(Either.Right(events))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "New York", page = 1)

        // Assert
        assertTrue(result is Either.Right)
        assertEquals(events, (result as Either.Right).value)
    }

    @Test
    fun `invoke handles null error message from repository gracefully`() = runTest {
        // Arrange
        coEvery { appRepository.getEvents(city = "Chicago", page = 0) }
            .returns(Either.Left(ApiResponse.HttpError("Error")))

        // Act
        val useCase = initializeUseCase()
        val result = useCase(city = "Chicago", page = 0)

        // Assert
        assertTrue(result is Either.Left)
        assertEquals("Error getting events, try again later", (result as Either.Left).value)
    }
}
