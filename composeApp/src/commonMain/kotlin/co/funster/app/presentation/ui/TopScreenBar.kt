package co.funster.app.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.funster.app.core.AppViewModel
import funster.composeapp.generated.resources.Res
import funster.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

/**
 * Composable function that displays a top app bar with the app name and a dropdown menu for selecting a city.
 * Integrates with [AppViewModel] to manage the currently selected city and update it when a new city is chosen.
 * Uses [TopAppBar] from Material 3 with an experimental API, as indicated by [ExperimentalMaterial3Api].
 *
 * @param viewModel The [AppViewModel] instance providing the selected city state and city update logic.
 * @param modifier Optional [Modifier] to apply to the [TopAppBar]. Defaults to an empty [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScreenBar(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Chicago", "Seattle", "New York", "Philadelphia", "Milwaukee", "Sayreville")
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()

    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets.systemBars,
        title = {
            Text(
                stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        actions = {
            Box {
                Text(
                    text = selectedCity,
                    modifier = Modifier
                        .padding(end = AppDimens.SmallPadding)
                        .clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                viewModel.setCity(city)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}
