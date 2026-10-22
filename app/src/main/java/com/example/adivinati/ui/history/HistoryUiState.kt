package com.example.adivinati.ui.history

import com.example.adivinati.data.model.GameResult

/**
 * Estado inmutable de la pantalla de historial.
 */
data class HistoryUiState(
    /** Partidas jugadas, de la más reciente a la más antigua. */
    val results: List<GameResult> = emptyList(),

    /** Promedio de puntos por partida. */
    val averageScore: Double = 0.0,

    /** Número total de partidas jugadas. */
    val totalGames: Int = 0,

    /** Mejor puntuación conseguida. */
    val bestScore: Int = 0,

    /** `true` mientras se lee el historial de DataStore. */
    val isLoading: Boolean = true,
) {
    /** `true` si todavía no se ha jugado ninguna partida. */
    val isEmpty: Boolean get() = !isLoading && results.isEmpty()
}
