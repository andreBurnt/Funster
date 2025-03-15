package co.funster.app.domain

import co.funster.app.core.util.ApiResponse
import co.funster.app.core.util.Either
import co.funster.app.data.AppRepository
import co.funster.app.core.model.Event
import io.mockative.Mockable

/**
 * Use case for retrieving events for a given city and page, mapping API errors to user-friendly messages.
 * Designed to be mockable for testing with Mockative.
 *
 * @param appRepository The repository to fetch events from.
 */
@Mockable
class GetEventsUseCase(
    private val appRepository: AppRepository,
) {
    /**
     * Executes the use case to fetch events for a city and page.
     *
     * @param city The city to fetch events for.
     * @param page The page number for pagination.
     * @return [Either.Right] with a list of [Event]s on success, or [Either.Left] with an error message.
     */
    suspend operator fun invoke(city: String, page: Int): Either<String, List<Event>> =
        when (val events = appRepository.getEvents(city = city, page = page)) {
            is Either.Left -> Either.Left(mapApiError(events.value))
            is Either.Right -> Either.Right(events.value)
        }

    /**
     * Maps [ApiResponse] errors to concise, user-friendly strings.
     *
     * @param error The [ApiResponse] error to map.
     * @return A string describing the error.
     */
    private fun mapApiError(error: ApiResponse): String = when (error) {
        is ApiResponse.IOException -> "Network unavailable"
        is ApiResponse.HttpError -> "Error getting events, try again later"
    }
}
