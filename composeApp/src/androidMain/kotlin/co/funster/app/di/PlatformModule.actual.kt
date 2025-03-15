package co.funster.app.di

import co.funster.app.data.createSettings
import co.funster.app.db.getDatabaseBuilder
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single { getDatabaseBuilder(androidContext()) }
    single {
        HttpClient(CIO) {
            expectSuccess = true // Throw errors for non 2xx responses
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Ignore unknown fields in the JSON
                    isLenient = true // Allow relaxed parsing (e.g., non-strict JSON)
                    coerceInputValues = true // Coerce missing/null values to defaults
                })
            }
        }
    }
    single<Settings> {
        createSettings(androidContext())
    }
}
