package com.example.adivinati.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.adivinati.data.json.JsonMappers
import com.example.adivinati.data.model.Riddle
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Data source de las adivinanzas creadas por el usuario.
 *
 * Persistencia: Preferences DataStore. El valor guardado es un `String` con un
 * array JSON de adivinanzas (no se usa SQLite ni Room en ningún punto).
 */
class UserRiddlesDataSource(private val dataStore: DataStore<Preferences>) {

    /** Flujo con las adivinanzas creadas por el usuario, actualizado en cada cambio. */
    val userRiddles: Flow<List<Riddle>> = dataStore.data
        .catch { throwable ->
            // Si el fichero está corrupto se emite un estado vacío en vez de romper la app.
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences ->
            preferences[KEY_USER_RIDDLES]?.let { decodeSafely(it) } ?: emptyList()
        }

    /** Añade una adivinanza al final de la lista persistida. */
    suspend fun addRiddle(riddle: Riddle) {
        dataStore.edit { preferences ->
            val current = preferences[KEY_USER_RIDDLES]?.let { decodeSafely(it) } ?: emptyList()
            preferences[KEY_USER_RIDDLES] = JsonMappers.encodeRiddles(current + riddle)
        }
    }

    private fun decodeSafely(json: String): List<Riddle> =
        runCatching { JsonMappers.parseRiddles(json) }.getOrDefault(emptyList())

    companion object {
        private val KEY_USER_RIDDLES = stringPreferencesKey("user_riddles")
    }
}
