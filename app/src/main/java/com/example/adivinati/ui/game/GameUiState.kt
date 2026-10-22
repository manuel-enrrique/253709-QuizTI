package com.example.adivinati.ui.game

import com.example.adivinati.core.GameRules
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.Riddle

/**
 * Estado inmutable de la pantalla de juego.
 *
 * Contiene toda la información que la UI necesita para dibujarse. La UI nunca
 * calcula puntajes ni decide el avance: eso es trabajo del [GameViewModel].
 */
data class GameUiState(
    /** Dificultad de la ronda en curso. */
    val difficulty: Difficulty = Difficulty.FACIL,

    /** Adivinanzas de la ronda (5). */
    val riddles: List<Riddle> = emptyList(),

    /** Índice de la adivinanza actual. */
    val currentIndex: Int = 0,

    /** Puntos acumulados en la ronda. */
    val score: Int = 0,

    /** Número de aciertos de la ronda. */
    val correctAnswers: Int = 0,

    /** `true` cuando el jugador ya ha visto la pista. */
    val hintVisible: Boolean = false,

    /** Índice de la opción elegida, o `null` si todavía no se ha respondido. */
    val selectedOption: Int? = null,

    /** `true` si la última respuesta fue correcta. */
    val isCorrect: Boolean = false,

    /** `true` mientras se prepara la ronda. */
    val isLoading: Boolean = true,

    /** `true` cuando la ronda ha terminado (el resultado ya está guardado). */
    val isFinished: Boolean = false,
) {
    /** Adivinanza que se está mostrando ahora mismo. */
    val currentRiddle: Riddle? get() = riddles.getOrNull(currentIndex)

    /** `true` si el jugador ya respondió a la pregunta actual. */
    val isAnswered: Boolean get() = selectedOption != null

    /** Total de preguntas de la ronda. */
    val totalQuestions: Int get() = riddles.size

    /** `true` si la pregunta actual es la última de la ronda. */
    val isLastQuestion: Boolean get() = totalQuestions > 0 && currentIndex == totalQuestions - 1

    /** Puntos otorgados por la última respuesta (0 si aún no ha respondido). */
    val lastAnswerPoints: Int get() = GameRules.pointsFor(
        isCorrect = isCorrect,
        hintUsed = hintVisible,
    )
}

/** Eventos de una sola vez emitidos por el [GameViewModel] (navegación, animaciones). */
sealed interface GameEvent {
    /** La ronda ha terminado: hay que mostrar la pantalla de resultado. */
    data object Finished : GameEvent
}
