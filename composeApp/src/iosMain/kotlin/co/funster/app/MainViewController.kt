package co.funster.app

import androidx.compose.ui.window.ComposeUIViewController
import co.funster.app.core.App
import co.funster.app.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModule)
        }
    }
) {
    App()
}