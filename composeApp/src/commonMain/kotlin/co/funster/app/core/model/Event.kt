package co.funster.app.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val startDate: String?,
    val endDate: String?,
    val city: String?,
    val location: String?
) {
    val formattedStartDate: String?
        get() = startDate?.let { formatDateString(it) }

    val formattedEndDate: String?
        get() = endDate?.let { formatDateString(it) }

    private fun formatDateString(dateString: String): String? {
        return try {
            val localDate = LocalDate.parse(dateString)
            val month = localDate.month.name.take(3).uppercase()
            val day = localDate.dayOfMonth.toString()
            val year = localDate.year.toString()
            "$month $day, $year"
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun fromApiEvent(apiEvent: ApiEvent): Event {
            val venue = apiEvent.embedded?.venues?.firstOrNull()
            var city: String? = null
            val location = buildString {
                if (venue != null) {
                    append(venue.name)
                    if (venue.city != null && venue.state != null) {
                        append(", ${venue.city.name}, ${venue.state.stateCode}")
                        city = venue.city.name
                    }
                }
            }.takeIf { it.isNotBlank() }


            return Event(
                id = apiEvent.id,
                name = apiEvent.name,
                imageUrl = apiEvent.images.firstOrNull()?.url,
                startDate = apiEvent.dates?.start?.localDate,
                endDate = apiEvent.dates?.end?.localDate,
                location = location,
                city = city
            )
        }
    }
}