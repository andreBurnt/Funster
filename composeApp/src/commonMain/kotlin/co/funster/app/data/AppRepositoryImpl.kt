package co.funster.app.data

import co.funster.app.core.model.Event
import co.funster.app.core.util.ApiResponse
import co.funster.app.core.util.Either
import co.funster.app.db.LocalDataSource
import co.funster.app.network.RemoteDataSource
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

/**
 * Implementation of [AppRepository] that fetches events from a remote source and caches them locally.
 * Handles network calls via [RemoteDataSource] and local storage via [LocalDataSource], with offline
 * fallback support. Uses coroutines for asynchronous operations and logs key actions.
 *
 * @param remoteDataSource The source for fetching events from the network.
 * @param localDataSource The source for caching and retrieving events locally.
 */
class AppRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : AppRepository {
    private val log = Logger.withTag(this::class.simpleName!!)

    /**
     * Retrieves events for a given city and page, attempting a network call first and falling back
     * to cached data if offline. Results are wrapped in [Either] to indicate success or failure.
     *
     * @param city The city to fetch events for.
     * @param page The page number for pagination.
     * @return [Either.Right] with a list of [Event]s on success, or [Either.Left] with an [ApiResponse] error.
     */
    override suspend fun getEvents(city: String, page: Int): Either<ApiResponse, List<Event>> =
        withContext(Dispatchers.IO) {
            val message = "Error getting events for city: $city, page: $page"
            try {
                // Attempt network call
                val response = remoteDataSource.getEvents(city = city, page = page)
                log.d("Successfully fetched ${response.embedded?.events?.size ?: 0} events from API")
                val events = response.embedded?.events?.map { Event.fromApiEvent(it) } ?: emptyList()
                if (events.isNotEmpty()) {
                    // Save to database in a background coroutine
                    CoroutineScope(Dispatchers.IO).launch {
                        localDataSource.saveEvents(events)
                        log.d("Saved ${events.size} events to local database")
                    }
                }
                Either.Right(events)
            } catch (e: IllegalArgumentException) {
                // Catching runtime errors, otherwise when device is offline
                // we get java.nio.channels.UnresolvedAddressException
                log.e(e) { "Illegal argument exception: $message" }
                handleOfflineFallback(city)
            } catch (e: IOException) {
                log.e(e) { "IO exception: $message" }
                handleOfflineFallback(city)
            } catch (e: Exception) {
                log.e(e) { "Unexpected error: $message" }
                Either.Left(ApiResponse.HttpError("Unexpected error: ${e.message}"))
            }
        }

    /**
     * Handles fallback to cached events when the network call fails (e.g., offline scenarios).
     *
     * @param city The city to retrieve cached events for.
     * @return [Either.Right] with cached [Event]s if available, or [Either.Left] with an error message.
     */
    private suspend fun handleOfflineFallback(city: String): Either<ApiResponse, List<Event>> {
        val cachedEvents = localDataSource.getEvents(city)
        return if (cachedEvents.isNotEmpty()) {
            log.d("Falling back to ${cachedEvents.size} cached events for city: $city")
            Either.Right(cachedEvents)
        } else {
            log.w("No cached events found for city: $city")
            Either.Left(ApiResponse.IOException("No internet and no cached events for city: $city"))
        }
    }
}
