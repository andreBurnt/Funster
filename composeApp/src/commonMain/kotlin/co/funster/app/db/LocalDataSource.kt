package co.funster.app.db

import co.funster.app.core.model.Event
import io.mockative.Mockable

/**
 * Interface defining the contract for local data storage and retrieval of events.
 */
@Mockable
interface LocalDataSource {
    /**
     * Retrieves cached events for a given city.
     *
     * @param city The city to fetch events for.
     * @return A list of [Event]s from local storage.
     */
    suspend fun getEvents(city: String): List<Event>

    /**
     * Saves a list of events to local storage.
     *
     * @param events The list of [Event]s to save.
     */
    suspend fun saveEvents(events: List<Event>)
}