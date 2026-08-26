package br.com.mykytadu

import android.app.Application
import android.util.Log
import br.com.mykytadu.di.initializeKoin

class MykytaDuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MykytaDu","Inicializando Koin")
        initializeKoin()
        Log.d("MykytaDu","Koin Inicializado")
    }
}