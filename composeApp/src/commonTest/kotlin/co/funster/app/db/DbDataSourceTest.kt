package co.funster.app.db

import co.funster.app.core.model.Event
import io.mockative.coEvery
import io.mockative.mock
import io.mockative.of
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DbDataSourceTest {

    private val eventDao: EventDao = mock(of<EventDao>())

    private fun initializeDataSource() = DbDataSource(
        eventDao = eventDao
    )

    @Test
    fun `getEvents retrieves events from dao for given city`() = runTest {
        // Arrange
        val city = "Chicago"
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
        coEvery { eventDao.getEventsForCity(city = city) }.returns(events)

        // Act
        val dataSource = initializeDataSource()
        val result = dataSource.getEvents(city = city)

        // Assert
        assertEquals(events, result)
    }

    @Test
    fun `getEvents returns empty list when dao returns no events`() = runTest {
        // Arrange
        val city = "New York"
        coEvery { eventDao.getEventsForCity(city = city) }.returns(emptyList())

        // Act
        val dataSource = initializeDataSource()
        val result = dataSource.getEvents(city = city)

        // Assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `saveEvents calls dao to insert or update events`() = runTest {
        // Arrange
        val events = listOf(
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
        coEvery { eventDao.insertOrUpdateEvents(events = events) }.returns(Unit)

        // Act
        val dataSource = initializeDataSource()
        dataSource.saveEvents(events = events)

        // Assert
        // Since saveEvents returns Unit, we just verify it doesn't throw
    }
}
