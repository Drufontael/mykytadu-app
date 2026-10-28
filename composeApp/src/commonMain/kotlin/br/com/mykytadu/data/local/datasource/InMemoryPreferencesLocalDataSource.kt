package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.PreferencesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementação em memória e reativa de [PreferencesLocalDataSource].
 * Garante concorrência thread-safe através de [Mutex] e atualizações reativas via [MutableStateFlow].
 */
class InMemoryPreferencesLocalDataSource(
    initialPreferences: PreferencesEntity = PreferencesEntity(),
) : PreferencesLocalDataSource {

    private val mutex = Mutex()
    private val preferencesFlow = MutableStateFlow(initialPreferences)

    override suspend fun getPreferences(): PreferencesEntity = mutex.withLock {
        preferencesFlow.value
    }

    override suspend fun savePreferences(preferences: PreferencesEntity): Unit = mutex.withLock {
        preferencesFlow.value = preferences
    }

    override fun observePreferences(): Flow<PreferencesEntity> =
        preferencesFlow.asStateFlow()
}
