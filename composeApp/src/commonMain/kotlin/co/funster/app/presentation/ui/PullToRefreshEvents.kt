package co.funster.app.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.funster.app.core.model.Event
import funster.composeapp.generated.resources.Res
import funster.composeapp.generated.resources.loading_more_message
import org.jetbrains.compose.resources.stringResource

/**
 * Composable function that displays a list of events with pull-to-refresh and infinite scrolling capabilities.
 * Uses [PullToRefreshBox] for refreshing and [LazyColumn] for efficient event rendering, triggering
 * additional event loading when the user scrolls near the bottom of the list.
 *
 * This composable is marked as [ExperimentalMaterial3Api] due to its use of experimental Material 3 APIs.
 *
 * @param events The list of [Event] objects to display in the [LazyColumn].
 * @param isRefreshing Boolean indicating whether the list is currently refreshing (e.g., via pull-to-refresh).
 * @param isLoadingMore Boolean indicating whether additional events are being loaded (e.g., via infinite scroll).
 * @param onRefresh Callback invoked when the user triggers a pull-to-refresh action.
 * @param onLoadMore Callback invoked when the user scrolls near the bottom of the list to load more events.
 * @param modifier Optional [Modifier] to apply to the [PullToRefreshBox]. Defaults to an empty [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshEvents(
    events: List<Event>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    // Detect when the user scrolls near the bottom of the list
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= events.size - 4
        }
    }

    // Trigger load more when the user scrolls to the bottom
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore && !isRefreshing) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter).padding(AppDimens.MediumPadding),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(events) { _, event ->
                EventItem(
                    event = event,
                    modifier = Modifier.padding(AppDimens.SmallPadding),
                )
            }
            if (isLoadingMore) {
                item {
                    Text(
                        text = stringResource(Res.string.loading_more_message),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimens.MediumPadding),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
