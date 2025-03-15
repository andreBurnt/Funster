package co.funster.app.di

import org.koin.dsl.module

val appModule = module {
    includes(sharedModule, platformModule())
}