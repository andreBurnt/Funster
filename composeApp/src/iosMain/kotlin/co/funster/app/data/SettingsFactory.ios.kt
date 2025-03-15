package co.funster.app.data

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

@OptIn(ExperimentalSettingsImplementation::class)
fun createSettings(): Settings {
    return KeychainSettings(service = "secret_shared_prefs")
}
