package br.com.mykytadu.di

import org.koin.core.context.startKoin

fun initializeKoin() {
    startKoin {
        modules(
            NetworkModule
        )
    }
}