package co.funster.app.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.funster.app.core.util.Either
import co.funster.app.core.model.Event
import co.funster.app.domain.GetEventsUseCase
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed class HomePageUiState {
    data object Empty: HomePageUiState()
    data object Loading: HomePageUiState()
    data class EventsLoaded(val events: List<Event>, val isLoadingMore: Boolean = false): HomePageUiState()
    data class PostCreated(val postId: String): HomePageUiState()
}

/**
 * ViewModel for managing the home page UI state, including event loading, pagination, and search filtering.
 * Uses [GetEventsUseCase] to fetch events and exposes state via Flows.
 *
 * @param getEventsUseCase The use case for retrieving events.
 * @param settings The settings wrapper (currently unused).
 */
class AppViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val settings: SettingsWrapper,
) : ViewModel() {
    companion object {
        private const val FLOW_STOP_TIMEOUT_MS = 5_000L
    }

    private val log = Logger.withTag(this::class.simpleName!!)

    private var _uiState = MutableStateFlow<HomePageUiState>(HomePageUiState.Loading)
    val uiState: StateFlow<HomePageUiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
            initialValue = HomePageUiState.Loading
        )
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCity = MutableStateFlow("Chicago")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allEvents: List<Event> = emptyList()

    private var currentPage: Int = 0

    init {
        log.d("Initializing view model")
        refreshEvents()
    }

    /**
     * Sets the selected city and triggers a refresh of events.
     *
     * @param city The new city to fetch events for.
     */
    fun setCity(city: String) {
        _selectedCity.value = city
        refreshEvents()
    }

    /**
     * Updates the search query and filters the currently loaded events.
     *
     * @param query The search query to filter events by.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // Update uiState with the filtered events
        log.d("Filtering events for: $query")
        _uiState.update {
            when (it) {
                is HomePageUiState.EventsLoaded -> HomePageUiState.EventsLoaded(filterEvents())
                else -> it // Keep the current state if not EventsLoaded
            }
        }
    }

    /**
     * Refreshes the event list by resetting pagination and reloading from the first page.
     */
    fun refreshEvents() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            _uiState.update { HomePageUiState.Loading }
            currentPage = 0
            allEvents = emptyList()
            loadEventsForPage()
            _isRefreshing.value = false
        }
    }

    /**
     * Loads the next page of events if not already loading more.
     */
    fun loadMoreEvents() {
        viewModelScope.launch {
            if (_uiState.value is HomePageUiState.EventsLoaded && !(_uiState.value as HomePageUiState.EventsLoaded).isLoadingMore) {
                _uiState.update { (it as HomePageUiState.EventsLoaded).copy(isLoadingMore = true) }
                currentPage++
                loadEventsForPage()
            }
        }
    }

    private suspend fun loadEventsForPage() {
        val city = _selectedCity.value
        log.d("Loading events.. city=$city, page=$currentPage")
        when (val result = getEventsUseCase(city, currentPage)) {
            is Either.Left -> {
                log.e { "Failed to load events: ${result.value}" }
                _errorMessage.value = "Failed to load events: ${result.value}"
                if (allEvents.isEmpty()) {
                    _uiState.update { HomePageUiState.Empty }
                } else {
                    _uiState.update { HomePageUiState.EventsLoaded(allEvents, isLoadingMore = false) }
                }
            }
            is Either.Right -> {
                log.i("Loaded events with IDs: ${result.value.map { it.id } }")
                val newEvents = result.value
                allEvents = if (currentPage == 0) newEvents else allEvents + newEvents
                _uiState.update { HomePageUiState.EventsLoaded(filterEvents(allEvents), isLoadingMore = false) }
            }
        }
    }

    /**
     * Dismisses the current error message.
     */
    fun dismissError() {
        _errorMessage.value = null
    }

    private fun filterEvents(events: List<Event> = allEvents): List<Event> {
        val query = _searchQuery.value.lowercase()
        return if (query.isEmpty()) events
        else events.filter {
            it.name.lowercase().contains(query) || (it.location?.lowercase()?.contains(query) ?: false)
        }
    }
}
