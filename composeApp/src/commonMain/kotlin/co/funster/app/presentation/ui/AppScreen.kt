package co.funster.app.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.funster.app.core.AppViewModel
import co.funster.app.core.HomePageUiState
import funster.composeapp.generated.resources.Res
import funster.composeapp.generated.resources.search_events_message
import org.jetbrains.compose.resources.stringResource

/**
 * Composable function that represents the main screen of the Funster app.
 *
 * This screen displays a list of events based on the UI state provided by the [AppViewModel].
 * It supports different states such as loading, displaying events, or showing an empty state.
 * The screen includes a top bar, a search bar, and a pull-to-refresh list of events.
 * It also displays transient error messages when applicable.
 *
 * @param viewModel The [AppViewModel] instance that provides the UI state and handles user actions.
 */
@Composable
fun AppScreen(
    viewModel: AppViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is HomePageUiState.PostCreated) viewModel.refreshEvents()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is HomePageUiState.EventsLoaded -> {
                val events = (uiState as HomePageUiState.EventsLoaded).events
                val isLoadingMore = (uiState as HomePageUiState.EventsLoaded).isLoadingMore

                Scaffold(
                    topBar = { TopScreenBar(viewModel = viewModel) },
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.padding(AppDimens.SmallPadding)
                        )
                        PullToRefreshEvents(
                            events = events,
                            isRefreshing = isRefreshing,
                            isLoadingMore = isLoadingMore,
                            onRefresh = { viewModel.refreshEvents() },
                            onLoadMore = { viewModel.loadMoreEvents() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            is HomePageUiState.Empty -> {
                Scaffold(
                    topBar = { TopScreenBar(viewModel = viewModel) },
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.padding(AppDimens.SmallPadding)
                        )
                        PullToRefreshEvents(
                            events = emptyList(), // Allow pull-to-refresh even when empty
                            isRefreshing = isRefreshing,
                            isLoadingMore = false,
                            onRefresh = { viewModel.refreshEvents() },
                            onLoadMore = { /* No-op for empty state */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            is HomePageUiState.Loading -> LoadingScreen()
            is HomePageUiState.PostCreated -> Unit // Handled in Launched effects
        }

        TransientMessage(
            message = errorMessage,
            onDismiss = viewModel::dismissError,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Composable function that displays a loading screen with a centered circular progress indicator.
 *
 * This screen is shown when the app is in a loading state, such as when fetching events from the network.
 *
 * @param modifier The [Modifier] to be applied to the loading screen. Defaults to [Modifier].
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp,
        )
    }
}

/**
 * Composable function that displays a search bar for filtering events.
 *
 * The search bar allows users to input a query to filter events by name or location.
 * It uses an [OutlinedTextField] with a label and supports single-line input.
 *
 * @param query The current search query string.
 * @param onQueryChange Callback invoked when the search query changes. Provides the new query string.
 * @param modifier The [Modifier] to be applied to the search bar. Defaults to [Modifier].
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(Res.string.search_events_message)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}
