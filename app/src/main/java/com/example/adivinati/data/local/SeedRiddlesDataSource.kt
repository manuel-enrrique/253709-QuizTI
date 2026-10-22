package com.example.adivinati.data.local

import android.content.Context
import com.example.adivinati.data.json.JsonMappers
import com.example.adivinati.data.model.Riddle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Data source de las adivinanzas semilla.
 *
 * Simula la carga de un "archivo JSON local": el fichero `assets/riddles.json`
 * se lee del APK con un pequeño retardo artificial y se cachea en memoria,
 * de forma que sólo se lee una vez por sesión.
 */
class SeedRiddlesDataSource(private val context: Context) {

    private val mutex = Mutex()

    @Volatile
    private var cache: List<Riddle>? = null

    /**
     * Devuelve las adivinanzas semilla. La primera vez las carga del JSON local
     * (con retardo simulado); las siguientes las devuelve de la caché en memoria.
     */
    suspend fun loadSeedRiddles(): List<Riddle> = mutex.withLock {
        cache ?: withContext(Dispatchers.IO) {
            delay(SIMULATED_LOAD_DELAY_MS)
            val rawJson = context.assets.open(SEED_FILE_NAME).bufferedReader().use { it.readText() }
            JsonMappers.parseRiddles(rawJson).also { cache = it }
        }
    }

    companion object {
        /** Nombre del fichero JSON dentro de `app/src/main/assets`. */
        const val SEED_FILE_NAME = "riddles.json"

        /** Retardo artificial para simular una carga de red (aunque la app es offline). */
        const val SIMULATED_LOAD_DELAY_MS = 700L
    }
}
