package co.funster.app.network

import co.funster.app.core.model.EventsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Implementation of [RemoteDataSource] that uses Ktor's [HttpClient] to fetch events from a remote API.
 * Supports pagination and sorting by date.
 *
 * @param client The HTTP client for making network requests.
 * @param baseUrl The base URL of the API endpoint.
 * @param apiKey The API key for authentication.
 */
class NetworkRemoteDataSource(
    private val client: HttpClient,
    private val baseUrl: String,
    private val apiKey: String,
): RemoteDataSource {
    override suspend fun getEvents(city: String, page: Int, pageSize: Int): EventsResponse =
        client.get("$baseUrl/events.json") {
            parameter("apikey", apiKey)
            parameter("city", city)
            parameter("page", page)
            parameter("size", pageSize)
            parameter("sort", "date,asc")
        }.body()
}
