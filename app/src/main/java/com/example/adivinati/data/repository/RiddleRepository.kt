package com.example.adivinati.data.repository

import com.example.adivinati.core.GameRules
import com.example.adivinati.data.local.GameResultDataSource
import com.example.adivinati.data.local.SeedRiddlesDataSource
import com.example.adivinati.data.local.UserRiddlesDataSource
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult
import com.example.adivinati.data.model.Riddle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Repositorio único de la app (capa de datos).
 *
 * Es la única puerta de entrada a los datos: la capa de presentación (ViewModels)
 * nunca habla directamente con DataStore ni con los assets.
 *
 * Combina dos fuentes de adivinanzas:
 *  - la semilla leída de `assets/riddles.json` (en memoria, cacheada);
 *  - las adivinanzas creadas por el usuario, persistidas en Preferences DataStore.
 */
class RiddleRepository(
    private val seedDataSource: SeedRiddlesDataSource,
    private val userRiddlesDataSource: UserRiddlesDataSource,
    private val gameResultDataSource: GameResultDataSource,
    scope: CoroutineScope,
) {

    private val _seedRiddles = MutableStateFlow<List<Riddle>>(emptyList())

    /** Todas las adivinanzas disponibles: semilla + las creadas por el usuario. */
    val riddles: StateFlow<List<Riddle>> =
        combine(_seedRiddles.asStateFlow(), userRiddlesDataSource.userRiddles) { seed, user ->
            seed + user
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    /** Historial de partidas, de la más reciente a la más antigua. */
    val history: Flow<List<GameResult>> =
        gameResultDataSource.gameResults.map { results -> results.sortedByDescending { it.id } }

    /**
     * Carga (o devuelve de caché) las adivinanzas semilla.
     * @return la lista de adivinanzas semilla.
     */
    suspend fun loadSeedRiddles(): List<Riddle> {
        if (_seedRiddles.value.isNotEmpty()) return _seedRiddles.value
        val loaded = seedDataSource.loadSeedRiddles()
        _seedRiddles.value = loaded
        return loaded
    }

    /**
     * Prepara una ronda: filtra por dificultad, mezcla el orden y toma las
     * primeras [ROUND_SIZE] adivinanzas.
     */
    suspend fun getRound(difficulty: Difficulty): List<Riddle> {
        loadSeedRiddles()
        val pool = riddles.value.filter { it.difficulty == difficulty.key }
        return pool.shuffled().take(GameRules.ROUND_SIZE)
    }

    /**
     * Crea y persiste una adivinanza escrita por el usuario.
     * @return la adivinanza guardada (con el id asignado).
     */
    suspend fun addUserRiddle(
        text: String,
        hint: String,
        options: List<String>,
        correctIndex: Int,
        difficulty: Difficulty,
    ): Riddle {
        val nextId = (riddles.value.maxOfOrNull { it.id } ?: 0) + 1
        val riddle = Riddle(
            id = nextId,
            text = text,
            hint = hint,
            options = options,
            correctIndex = correctIndex,
            difficulty = difficulty.key,
            isUserCreated = true,
        )
        userRiddlesDataSource.addRiddle(riddle)
        return riddle
    }

    /** Guarda el resultado de una partida en el historial persistido. */
    suspend fun saveResult(result: GameResult) {
        gameResultDataSource.saveResult(result)
    }
}
