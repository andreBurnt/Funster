package co.funster.app.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import co.funster.app.presentation.navigation.AppNavigationGraph
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
    val appViewModel = koinViewModel<AppViewModel>()
    MaterialTheme {
        AppNavigationGraph(
           appViewModel = appViewModel,
        )
    }
}
