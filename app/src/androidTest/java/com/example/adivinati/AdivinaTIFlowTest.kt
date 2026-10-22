package com.example.adivinati

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test de integración end-to-end sobre la app real (`MainActivity`).
 *
 * Recorre el flujo completo del enunciado:
 *
 * ```
 * Menú -> Partida (5 preguntas) -> Resultado -> Menú -> Historial
 * ```
 *
 * y comprueba, sobre todo, que la partida se **persiste en DataStore**:
 * el historial crece en 1 tras la ronda y sigue ahí aunque se destruya y
 * vuelva a crear la Activity (equivalente a cerrar y reabrir la app).
 */
class AdivinaTIFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun rondaCompleta_seGuardaEnDataStoreYApareceEnElHistorial() {
        // --- 0. El menú carga la semilla local (hay un retardo simulado de 700 ms).
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("AdivinaTI").fetchSemanticsNodes().isNotEmpty()
        }
        val historyBefore = historySize()

        // --- 1. Jugar (dificultad por defecto: Fácil).
        composeRule.onAllNodesWithText("Jugar").onFirst().performClick()

        // --- 2. Las 5 preguntas de la ronda.
        repeat(5) { index ->
            composeRule.waitUntil(TIMEOUT_MS) {
                composeRule.onAllNodesWithText("Pregunta ${index + 1} de 5")
                    .fetchSemanticsNodes().isNotEmpty()
            }

            val nextLabel = if (index == 4) "Ver resultado" else "Siguiente"

            // Antes de responder, el botón de avance está deshabilitado.
            composeRule.onNodeWithText(nextLabel).assertIsNotEnabled()

            // Se responde siempre la opción A: el resultado no importa para el test,
            // lo que se valida es el flujo y la persistencia.
            composeRule.onAllNodesWithText("A").onFirst().performClick()
            composeRule.waitForIdle()

            // Ya se puede avanzar.
            composeRule.onNodeWithText(nextLabel).assertIsEnabled().performClick()
        }

        // --- 3. Pantalla de resultado.
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("Resultado de la partida")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Puntos").assertIsDisplayed()
        composeRule.onNodeWithText("Aciertos").assertIsDisplayed()
        composeRule.onNodeWithText("Volver al menú").assertIsDisplayed()

        // --- 4. Volver al menú y abrir el historial.
        composeRule.onNodeWithText("Volver al menú").performClick()
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("Ver Historial").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Ver Historial").onFirst().performClick()

        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("Historial de partidas")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Mejor:", substring = true).assertIsDisplayed()

        // --- 5. La partida se ha guardado en DataStore (y sólo ha crecido en 1).
        assertEquals(historyBefore + 1, historySize())
    }

    @Test
    fun elHistorialSobreviveADestruirYRecrearLaActivity() {
        // Se juega una ronda para tener al menos una partida guardada.
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("AdivinaTI").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Jugar").onFirst().performClick()

        repeat(5) { index ->
            composeRule.waitUntil(TIMEOUT_MS) {
                composeRule.onAllNodesWithText("Pregunta ${index + 1} de 5")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            val nextLabel = if (index == 4) "Ver resultado" else "Siguiente"
            composeRule.onAllNodesWithText("A").onFirst().performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithText(nextLabel).performClick()
        }

        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("Resultado de la partida")
                .fetchSemanticsNodes().isNotEmpty()
        }
        val savedCount = historySize()
        assertTrue("debería haber al menos una partida guardada", savedCount >= 1)

        // Se destruye y recrea la Activity (equivale a cerrar y reabrir la app).
        composeRule.activityRule.scenario.recreate()
        composeRule.waitUntil(TIMEOUT_MS) {
            runCatching {
                composeRule.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            }.getOrDefault(false)
        }

        // El historial sigue ahí: está persistido en DataStore, no en memoria.
        assertEquals(savedCount, historySize())
    }

    @Test
    fun elFormularioValidaLosCamposVacios() {
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("AdivinaTI").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Agregar Adivinanza").onFirst().performClick()

        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodesWithText("Nueva adivinanza").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Guardar adivinanza").onFirst().performClick()
        composeRule.waitForIdle()

        // Los cuatro mensajes de error del ViewModel se muestran en pantalla.
        composeRule.onNodeWithText("Escribe el enunciado").assertExists()
        composeRule.onNodeWithText("Escribe una pista").assertExists()
        composeRule.onNodeWithText("Las 4 opciones son obligatorias").assertExists()
        composeRule.onNodeWithText("Selecciona la opción correcta").assertExists()

        // Y no se ha navegado fuera del formulario.
        composeRule.onNodeWithText("Nueva adivinanza").assertExists()
    }

    /** Número de partidas guardadas en DataStore en este momento. */
    private fun historySize(): Int = runBlocking {
        val app = composeRule.activity.application as AdivinaTIApplication
        app.container.riddleRepository.history.first().size
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
    }
}
