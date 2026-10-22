package com.example.adivinati.core

/**
 * Reglas de juego de AdivinaTI, centralizadas para que la capa de datos y la
 * capa de presentación appliquen exactamente las mismas cifras.
 */
object GameRules {

    /** Adivinanzas por ronda. */
    const val ROUND_SIZE = 5

    /** Puntos por acertar sin haber pedido pista. */
    const val POINTS_WITHOUT_HINT = 2

    /** Puntos por acertar después de pedir pista. */
    const val POINTS_WITH_HINT = 1

    /** Puntos por fallar. */
    const val POINTS_WRONG = 0

    /** Puntuación máxima de una ronda: [ROUND_SIZE] x [POINTS_WITHOUT_HINT] = 10. */
    const val MAX_SCORE = ROUND_SIZE * POINTS_WITHOUT_HINT

    /**
     * Puntos que otorga una respuesta.
     *
     * @param isCorrect `true` si la opción elegida era la correcta.
     * @param hintUsed  `true` si el jugador ya había pedido pista.
     */
    fun pointsFor(isCorrect: Boolean, hintUsed: Boolean): Int = when {
        !isCorrect -> POINTS_WRONG
        hintUsed -> POINTS_WITH_HINT
        else -> POINTS_WITHOUT_HINT
    }
}
