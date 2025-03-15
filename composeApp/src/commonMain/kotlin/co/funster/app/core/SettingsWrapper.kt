package co.funster.app.core

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.mockative.Mockable

@Mockable
interface SettingsWrapper {
    fun getStringOrNull(key: String): String?
    fun setString(key: String, value: String)
}

class SettingsImpl(private val settings: Settings) : SettingsWrapper {
    override fun getStringOrNull(key: String): String? = settings[key]
    override fun setString(key: String, value: String) {
        settings[key] = value
    }
}
