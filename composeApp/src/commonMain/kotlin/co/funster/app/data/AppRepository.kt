package co.funster.app.data

import co.funster.app.core.model.Event
import co.funster.app.core.util.ApiResponse
import co.funster.app.core.util.Either

interface AppRepository {
    suspend fun getEvents(city: String, page: Int): Either<ApiResponse, List<Event>>
}