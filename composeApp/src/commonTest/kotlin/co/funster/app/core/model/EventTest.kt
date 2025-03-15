package co.funster.app.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EventTest {

    @Test
    fun `fromApiEvent maps full ApiEvent correctly`() {
        // Arrange
        val apiEvent = ApiEvent(
            name = "Concert A",
            type = "event",
            id = "1",
            test = false,
            url = "https://example.com/event/1",
            locale = "en-us",
            images = listOf(Image(url = "https://example.com/image1.jpg")),
            dates = Dates(
                start = DateTime(localDate = "2025-03-15"),
                end = DateTime(localDate = "2025-03-16")
            ),
            embedded = EmbeddedEventDetails(
                venues = listOf(
                    Venue(
                        name = "Venue 1",
                        id = "v1",
                        city = City("Chicago"),
                        state = State("Illinois", "IL")
                    )
                )
            )
        )

        // Act
        val event = Event.fromApiEvent(apiEvent)

        // Assert
        assertEquals(
            Event(
                id = "1",
                name = "Concert A",
                imageUrl = "https://example.com/image1.jpg",
                startDate = "2025-03-15",
                endDate = "2025-03-16",
                city = "Chicago",
                location = "Venue 1, Chicago, IL"
            ),
            event
        )
    }

    @Test
    fun `fromApiEvent handles minimal ApiEvent with no venue`() {
        // Arrange
        val apiEvent = ApiEvent(
            name = "Theater B",
            type = "event",
            id = "2",
            test = null,
            url = null,
            locale = null,
            images = emptyList(),
            dates = Dates(start = DateTime(localDate = "2025-03-17")),
            embedded = null
        )

        // Act
        val event = Event.fromApiEvent(apiEvent)

        // Assert
        assertEquals(
            Event(
                id = "2",
                name = "Theater B",
                imageUrl = null,
                startDate = "2025-03-17",
                endDate = null,
                city = null,
                location = null
            ),
            event
        )
    }

    @Test
    fun `fromApiEvent uses first image when multiple provided`() {
        // Arrange
        val apiEvent = ApiEvent(
            name = "Festival C",
            type = "event",
            id = "3",
            images = listOf(
                Image(url = "https://example.com/image1.jpg"),
                Image(url = "https://example.com/image2.jpg")
            ),
            dates = null,
            embedded = null
        )

        // Act
        val event = Event.fromApiEvent(apiEvent)

        // Assert
        assertEquals(
            Event(
                id = "3",
                name = "Festival C",
                imageUrl = "https://example.com/image1.jpg",
                startDate = null,
                endDate = null,
                city = null,
                location = null
            ),
            event
        )
    }

    @Test
    fun `formattedStartDate formats valid date correctly`() {
        // Arrange
        val event = Event(
            id = "1",
            name = "Event 1",
            imageUrl = null,
            startDate = "2025-03-15",
            endDate = null,
            city = null,
            location = null
        )

        // Act & Assert
        assertEquals("MAR 15, 2025", event.formattedStartDate)
    }

    @Test
    fun `formattedEndDate formats valid date correctly`() {
        // Arrange
        val event = Event(
            id = "1",
            name = "Event 1",
            imageUrl = null,
            startDate = null,
            endDate = "2025-03-16",
            city = null,
            location = null
        )

        // Act & Assert
        assertEquals("MAR 16, 2025", event.formattedEndDate)
    }

    @Test
    fun `formattedStartDate returns null for invalid date`() {
        // Arrange
        val event = Event(
            id = "1",
            name = "Event 1",
            imageUrl = null,
            startDate = "invalid-date",
            endDate = null,
            city = null,
            location = null
        )

        // Act & Assert
        assertNull(event.formattedStartDate)
    }

    @Test
    fun `formattedEndDate returns null for null endDate`() {
        // Arrange
        val event = Event(
            id = "1",
            name = "Event 1",
            imageUrl = null,
            startDate = "2025-03-15",
            endDate = null,
            city = null,
            location = null
        )

        // Act & Assert
        assertNull(event.formattedEndDate)
    }

    @Test
    fun `fromApiEvent handles empty venue list`() {
        // Arrange
        val apiEvent = ApiEvent(
            name = "Show D",
            type = "event",
            id = "4",
            images = emptyList(),
            dates = Dates(start = DateTime(localDate = "2025-03-18")),
            embedded = EmbeddedEventDetails(venues = emptyList())
        )

        // Act
        val event = Event.fromApiEvent(apiEvent)

        // Assert
        assertEquals(
            Event(
                id = "4",
                name = "Show D",
                imageUrl = null,
                startDate = "2025-03-18",
                endDate = null,
                city = null,
                location = null
            ),
            event
        )
    }
}
