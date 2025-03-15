package co.funster.app.db

import co.funster.app.core.model.Event
import io.mockative.Mockable

/**
 * Implementation of [LocalDataSource] that uses Room's [EventDao] for local event storage and retrieval.
 *
 * @param eventDao The DAO for interacting with the events database table.
 */
@Mockable
class DbDataSource(private val eventDao: EventDao) : LocalDataSource {
    override suspend fun getEvents(city: String): List<Event> {
        return eventDao.getEventsForCity(city)
    }

    override suspend fun saveEvents(events: List<Event>) {
        return eventDao.insertOrUpdateEvents(events)
    }
}
