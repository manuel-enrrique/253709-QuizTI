package com.example.adivinati.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.adivinati.data.json.JsonMappers
import com.example.adivinati.data.model.GameResult
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Data source del historial de partidas.
 *
 * Persistencia: Preferences DataStore con un array JSON de [GameResult].
 */
class GameResultDataSource(private val dataStore: DataStore<Preferences>) {

    /** Flujo con el historial completo de partidas jugadas. */
    val gameResults: Flow<List<GameResult>> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences ->
            preferences[KEY_GAME_RESULTS]?.let { decodeSafely(it) } ?: emptyList()
        }

    /** Guarda un resultado nuevo al final del historial. */
    suspend fun saveResult(result: GameResult) {
        dataStore.edit { preferences ->
            val current = preferences[KEY_GAME_RESULTS]?.let { decodeSafely(it) } ?: emptyList()
            preferences[KEY_GAME_RESULTS] = JsonMappers.encodeGameResults(current + result)
        }
    }

    private fun decodeSafely(json: String): List<GameResult> =
        runCatching { JsonMappers.parseGameResults(json) }.getOrDefault(emptyList())

    companion object {
        private val KEY_GAME_RESULTS = stringPreferencesKey("game_results")
    }
}
