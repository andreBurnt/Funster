package co.funster.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.funster.app.core.AppViewModel
import co.funster.app.presentation.ui.AppScreen
import kotlinx.serialization.Serializable

/**
 * Defines a navigation route within the app. Implementations represent destinations in the navigation graph.
 * Marked as [Serializable] to support navigation argument serialization with Jetpack Navigation.
 */
@Serializable
sealed interface AppRoute

/**
 * Represents the home route of the app, serving as the default starting destination.
 * Implements [AppRoute] and is [Serializable] for navigation purposes.
 */
@Serializable
data object Home : AppRoute

/**
 * Composable function that sets up the navigation graph for the Funster app.
 * Uses Jetpack Navigation's [NavHost] to define routes and manage navigation state.
 *
 * @param modifier Optional [Modifier] to apply to the [NavHost]. Defaults to an empty [Modifier].
 * @param startDestination The initial [AppRoute] to display when the navigation graph is loaded.
 *                         Defaults to [Home].
 * @param appViewModel The [AppViewModel] instance providing UI state and logic for screens.
 * @param navController The [NavHostController] managing navigation state. Defaults to a new instance
 *                      created by [rememberNavController].
 */
@Composable
fun AppNavigationGraph(
    modifier: Modifier = Modifier,
    startDestination: AppRoute = Home,
    appViewModel: AppViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        startDestination = startDestination,
        navController = navController,
    ) {
        composable<Home> {
            AppScreen(
                viewModel = appViewModel,
                )
        }
    }
}
