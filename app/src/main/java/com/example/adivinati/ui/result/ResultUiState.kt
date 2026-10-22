package com.example.adivinati.ui.result

import com.example.adivinati.core.GameRules
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult

/**
 * Estado inmutable de la pantalla de resultado.
 */
data class ResultUiState(
    /** Resultado de la partida recién terminada (puede ser `null` si aún no carga). */
    val result: GameResult? = null,

    /** Dificultad con la que se jugó. */
    val difficulty: Difficulty = Difficulty.FACIL,

    /** `true` mientras se lee el resultado del repositorio. */
    val isLoading: Boolean = true,
) {
    /** Puntos conseguidos en la ronda. */
    val score: Int get() = result?.score ?: 0

    /** Número de aciertos. */
    val correctAnswers: Int get() = result?.correctAnswers ?: 0

    /** Fecha de la partida. */
    val date: String get() = result?.date.orEmpty()

    /** Porcentaje de aciertos sobre el máximo posible (0-100). */
    val percentage: Int get() = if (GameRules.MAX_SCORE == 0) 0 else (score * 100) / GameRules.MAX_SCORE
}
