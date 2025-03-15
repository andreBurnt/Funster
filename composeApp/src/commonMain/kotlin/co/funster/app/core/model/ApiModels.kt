package co.funster.app.core.model

import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventsResponse(
    @SerialName("_embedded") val embedded: EmbeddedEvents? = null,
    @SerialName("_links") val links: Links? = null,
    val page: Page? = null
)

@Serializable
data class EmbeddedEvents(
    val events: List<ApiEvent> = emptyList()
)

@Serializable
data class ApiEvent(
    val name: String,
    val type: String,
    @PrimaryKey val id: String,
    val test: Boolean? = null,
    val url: String? = null,
    val locale: String? = null,
    val images: List<Image> = emptyList(),
    val dates: Dates? = null,
    @SerialName("_embedded") val embedded: EmbeddedEventDetails? = null
)

@Serializable
data class EmbeddedEventDetails(
    val venues: List<Venue> = emptyList()
)

@Serializable
data class Venue(
    val name: String? = null,
    val type: String? = null,
    val id: String,
    val test: Boolean? = null,
    val url: String? = null,
    val locale: String? = null,
    val postalCode: String? = null,
    val timezone: String? = null,
    val city: City? = null,
    val state: State? = null,
    val country: Country? = null,
    val address: Address? = null,
    val location: Location? = null,
)

@Serializable
data class City(val name: String)

@Serializable
data class State(val name: String, val stateCode: String)

@Serializable
data class Country(val name: String, val countryCode: String)

@Serializable
data class Address(val line1: String)

@Serializable
data class Location(val longitude: String, val latitude: String)

@Serializable
data class Image(
    val ratio: String? = null,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val fallback: Boolean? = null,
)

@Serializable
data class Dates(
    val start: DateTime? = null,
    val end: DateTime? = null,
    val timezone: String? = null,
    val status: Status? = null,
    val spanMultipleDays: Boolean? = null,
)

@Serializable
data class DateTime(
    val localDate: String? = null,
    val localTime: String? = null,
    val dateTime: String? = null,
    val noSpecificTime: Boolean? = null,
)

@Serializable
data class Status(
    val code: String
)

@Serializable
data class Links(
    val first: Link? = null,
    val self: Link? = null,
    val next: Link? = null,
    val last: Link? = null
)

@Serializable
data class Page(
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int
)

@Serializable
data class Link(val href: String)
