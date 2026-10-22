package com.example.adivinati.data.json

import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult
import com.example.adivinati.data.model.Riddle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests de los conversores JSON <-> modelos de dominio.
 */
class JsonMappersTest {

    private val riddle = Riddle(
        id = 42,
        text = "¿Qué lenguaje usa \"fun\"?",
        hint = "Android lo usa de forma nativa",
        options = listOf("Java", "Kotlin", "Swift", "Dart"),
        correctIndex = 1,
        difficulty = Difficulty.MEDIA.key,
        isUserCreated = true,
    )

    @Test
    fun `una adivinanza sobrevive a un ciclo de codificacion y decodificacion`() {
        val json = JsonMappers.encodeRiddles(listOf(riddle))
        val decoded = JsonMappers.parseRiddles(json)

        assertEquals(1, decoded.size)
        assertEquals(riddle, decoded.first())
    }

    @Test
    fun `un historial sobrevive a un ciclo de codificacion y decodificacion`() {
        val results = listOf(
            GameResult(
                id = 1_700_000_000_000L,
                difficulty = Difficulty.FACIL.key,
                score = 8,
                correctAnswers = 4,
                date = "21/10/2026 14:32",
            ),
            GameResult(
                id = 1_700_000_100_000L,
                difficulty = Difficulty.DIFICIL.key,
                score = 3,
                correctAnswers = 2,
                date = "21/10/2026 15:10",
            ),
        )

        val decoded = JsonMappers.parseGameResults(JsonMappers.encodeGameResults(results))

        assertEquals(results, decoded)
    }

    @Test
    fun `un json vacio se decodifica como lista vacia`() {
        assertTrue(JsonMappers.parseRiddles("[]").isEmpty())
        assertTrue(JsonMappers.parseGameResults("[]").isEmpty())
    }
}
