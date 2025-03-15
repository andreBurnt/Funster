package co.funster.app.di

import Funster.composeApp.BuildConfig
import co.funster.app.core.AppViewModel
import co.funster.app.core.SettingsImpl
import co.funster.app.core.SettingsWrapper
import co.funster.app.data.AppRepository
import co.funster.app.data.AppRepositoryImpl
import co.funster.app.db.AppDatabase
import co.funster.app.db.DbDataSource
import co.funster.app.db.EventDao
import co.funster.app.db.LocalDataSource
import co.funster.app.db.getDatabase
import co.funster.app.db.getEventDao
import co.funster.app.domain.GetEventsUseCase
import co.funster.app.network.NetworkRemoteDataSource
import co.funster.app.network.RemoteDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    viewModelOf(::AppViewModel)
    singleOf(::GetEventsUseCase)
    singleOf(::SettingsImpl) {
        bind<SettingsWrapper>()
    }
}

val apiModule = module {
    single<RemoteDataSource> {
        NetworkRemoteDataSource(
            get(),
            BuildConfig.API_BASE_URL,
            BuildConfig.API_TOKEN
        )
    }
}

val repoModule = module {
    singleOf(::AppRepositoryImpl) {
       bind<AppRepository>()
    }
    singleOf(::DbDataSource) {
        bind<LocalDataSource>()
    }
    single<AppDatabase> {
        getDatabase(get())
    }
    single<EventDao> {
        getEventDao(get())
    }
}

val sharedModule = module {
    includes(coreModule, apiModule, repoModule)
}
