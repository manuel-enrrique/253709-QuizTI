package com.example.adivinati.data.local

import com.example.adivinati.data.json.JsonMappers
import com.example.adivinati.data.model.Difficulty
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests del fichero de adivinanzas semilla (`app/src/main/assets/riddles.json`).
 *
 * Verifican que el JSON local cumple el contrato de la app: 15 adivinanzas
 * (5 por dificultad), 4 opciones y una opción correcta válida en cada una.
 */
class SeedRiddlesTest {

    private val seedFile: File = File("src/main/assets/riddles.json")

    private val riddles = JsonMappers.parseRiddles(seedFile.readText())

    @Test
    fun `el fichero semilla existe y no esta vacio`() {
        assertTrue("No se encuentra ${seedFile.absolutePath}", seedFile.exists())
        assertTrue(riddles.isNotEmpty())
    }

    @Test
    fun `hay al menos cinco adivinanzas por dificultad`() {
        Difficulty.entries.forEach { difficulty ->
            val count = riddles.count { it.difficulty == difficulty.key }
            assertTrue("Faltan adivinanzas de dificultad ${difficulty.label}", count >= 5)
        }
    }

    @Test
    fun `hay al menos quince adivinanzas en total`() {
        assertTrue(riddles.size >= 15)
    }

    @Test
    fun `cada adivinanza tiene cuatro opciones y una correcta valida`() {
        riddles.forEach { riddle ->
            assertEquals("Enunciado vacío en ${riddle.id}", true, riddle.text.isNotBlank())
            assertEquals("Pista vacía en ${riddle.id}", true, riddle.hint.isNotBlank())
            assertEquals("Número de opciones en ${riddle.id}", 4, riddle.options.size)
            assertTrue("Opciones vacías en ${riddle.id}", riddle.options.none { it.isBlank() })
            assertTrue(
                "Índice correcto inválido en ${riddle.id}",
                riddle.correctIndex in riddle.options.indices,
            )
        }
    }

    @Test
    fun `los identificadores de las semillas son unicos`() {
        val ids = riddles.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `ninguna adivinanza semilla esta marcada como creada por el usuario`() {
        assertFalse(riddles.any { it.isUserCreated })
    }
}
