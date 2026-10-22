package com.example.adivinati.core

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests de las reglas de puntuación de AdivinaTI.
 */
class GameRulesTest {

    @Test
    fun `acertar sin pista suma dos puntos`() {
        assertEquals(2, GameRules.pointsFor(isCorrect = true, hintUsed = false))
    }

    @Test
    fun `acertar con pista suma un punto`() {
        assertEquals(1, GameRules.pointsFor(isCorrect = true, hintUsed = true))
    }

    @Test
    fun `fallar suma cero puntos`() {
        assertEquals(0, GameRules.pointsFor(isCorrect = false, hintUsed = false))
        assertEquals(0, GameRules.pointsFor(isCorrect = false, hintUsed = true))
    }

    @Test
    fun `la puntuacion maxima de una ronda es diez`() {
        assertEquals(10, GameRules.MAX_SCORE)
        assertEquals(5, GameRules.ROUND_SIZE)
    }
}
