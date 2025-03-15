package co.funster.app.network

import co.funster.app.core.model.EventsResponse
import io.mockative.Mockable

/**
 * Interface defining the contract for fetching events from a remote data source.
 */
@Mockable
interface RemoteDataSource {
    /**
     * Fetches events from a remote API for a specified city and page.
     *
     * @param city The city to fetch events for.
     * @param page The page number for pagination.
     * @param pageSize The number of events per page (defaults to 10).
     * @return An [EventsResponse] containing the fetched events and metadata.
     */
    suspend fun getEvents(city: String, page: Int, pageSize: Int = 10): EventsResponse
}
