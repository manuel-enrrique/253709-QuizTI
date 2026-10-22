package com.example.adivinati.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult
import com.example.adivinati.data.model.Riddle
import com.example.adivinati.ui.addriddle.AddRiddleContent
import com.example.adivinati.ui.addriddle.AddRiddleUiState
import com.example.adivinati.ui.game.GameContent
import com.example.adivinati.ui.game.GameUiState
import com.example.adivinati.ui.history.HistoryContent
import com.example.adivinati.ui.history.HistoryUiState
import com.example.adivinati.ui.menu.MenuContent
import com.example.adivinati.ui.menu.MenuUiState
import com.example.adivinati.ui.result.ResultContent
import com.example.adivinati.ui.result.ResultUiState
import com.example.adivinati.ui.theme.AdivinaTITheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests de UI de las pantallas "tontas" (los `*Content` sin estado propio).
 *
 * Al ser Composable puramente presentacionales, se pueden testear sin
 * ViewModel, sin DataStore y sin emulador con estado previo: el estado se
 * inyecta directamente. Así se comprueba el contrato de renderizado de cada
 * pantalla y, sobre todo, que la UI **no calcula nada por su cuenta**
 * (los puntos, el avance y los errores ya llegan resueltos en el `UiState`).
 */
class ScreensContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ------------------------------------------------------------- Menú

    @Test
    fun menu_muestraContadoresYLosTresBotones() {
        composeRule.setContent {
            AdivinaTITheme {
                MenuContent(
                    state = MenuUiState(
                        selectedDifficulty = Difficulty.FACIL,
                        availableRiddles = 5,
                        totalRiddles = 15,
                        isLoading = false,
                    ),
                    onDifficultySelected = {},
                    onPlay = {},
                    onAddRiddle = {},
                    onHistory = {},
                )
            }
        }

        composeRule.onNodeWithText("AdivinaTI").assertIsDisplayed()
        composeRule.onNodeWithText("Dificultad").assertIsDisplayed()
        composeRule.onNodeWithText("5 adivinanzas disponibles · 15 en total").assertIsDisplayed()
        composeRule.onNodeWithText("Jugar").assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithText("Agregar Adivinanza").assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithText("Ver Historial").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun menu_mientrasCarga_muestraMensajeYBloqueaJugar() {
        composeRule.setContent {
            AdivinaTITheme {
                MenuContent(
                    state = MenuUiState(isLoading = true),
                    onDifficultySelected = {},
                    onPlay = {},
                    onAddRiddle = {},
                    onHistory = {},
                )
            }
        }

        composeRule.onNodeWithText("Cargando adivinanzas...").assertIsDisplayed()
        composeRule.onNodeWithText("Jugar").assertIsNotEnabled()
        // Los otros dos botones siguen disponibles durante la carga.
        composeRule.onNodeWithText("Agregar Adivinanza").assertIsEnabled()
        composeRule.onNodeWithText("Ver Historial").assertIsEnabled()
    }

    @Test
    fun menu_cambiarDificultad_devuelveLaSeleccionAlPadre() {
        var selected: Difficulty? = null
        composeRule.setContent {
            AdivinaTITheme {
                MenuContent(
                    state = MenuUiState(availableRiddles = 5, totalRiddles = 15, isLoading = false),
                    onDifficultySelected = { selected = it },
                    onPlay = {},
                    onAddRiddle = {},
                    onHistory = {},
                )
            }
        }

        composeRule.onNodeWithText("Media").performClick()
        composeRule.runOnIdle { assertEquals(Difficulty.MEDIA, selected) }

        composeRule.onNodeWithText("Difícil").performClick()
        composeRule.runOnIdle { assertEquals(Difficulty.DIFICIL, selected) }
    }

    // ------------------------------------------------------------- Juego

    @Test
    fun juego_muestraEnunciadoCuatroOpcionesYPuntuacion() {
        composeRule.setContent {
            AdivinaTITheme {
                GameContent(
                    state = GameUiState(
                        difficulty = Difficulty.FACIL,
                        riddles = listOf(RIDDLE),
                        currentIndex = 0,
                        isLoading = false,
                    ),
                    onHintRequested = {},
                    onOptionSelected = {},
                    onNextQuestion = {},
                    onExit = {},
                )
            }
        }

        composeRule.onNodeWithText("AdivinaTI · Fácil").assertIsDisplayed()
        composeRule.onNodeWithText("Pregunta 1 de 5").assertIsDisplayed()
        composeRule.onNodeWithText("0 / 10 pts").assertIsDisplayed()
        composeRule.onNodeWithText(RIDDLE.text).assertIsDisplayed()
        composeRule.onNodeWithText("Pedir pista (acierto = 1 punto)").assertIsDisplayed()

        // Las 4 opciones se muestran, cada una con su letra (A, B, C, D).
        composeRule.onAllNodesWithText("A").assertCountEquals(1)
        composeRule.onAllNodesWithText("B").assertCountEquals(1)
        composeRule.onAllNodesWithText("C").assertCountEquals(1)
        composeRule.onAllNodesWithText("D").assertCountEquals(1)
        composeRule.onNodeWithText("Kotlin").assertIsDisplayed()

        // Sin respuesta todavía no se puede avanzar.
        composeRule.onNodeWithText("Siguiente").assertIsNotEnabled()
    }

    @Test
    fun juego_conPista_laMuestraYLaPuntuacionBajaA1Punto() {
        composeRule.setContent {
            AdivinaTITheme {
                GameContent(
                    state = GameUiState(
                        difficulty = Difficulty.FACIL,
                        riddles = listOf(RIDDLE),
                        currentIndex = 0,
                        score = 1,
                        correctAnswers = 1,
                        hintVisible = true,
                        selectedOption = 1,
                        isCorrect = true,
                        isLoading = false,
                    ),
                    onHintRequested = {},
                    onOptionSelected = {},
                    onNextQuestion = {},
                    onExit = {},
                )
            }
        }

        composeRule.onNodeWithText("Pista").assertIsDisplayed()
        composeRule.onNodeWithText(RIDDLE.hint).assertIsDisplayed()
        composeRule.onNodeWithText("¡Correcto! +1 puntos").assertIsDisplayed()
        composeRule.onNodeWithText("1 / 10 pts").assertIsDisplayed()
        // Ya se puede avanzar.
        composeRule.onNodeWithText("Siguiente").assertIsEnabled()
    }

    @Test
    fun juego_sinPista_acertoSuma2Puntos() {
        composeRule.setContent {
            AdivinaTITheme {
                GameContent(
                    state = GameUiState(
                        difficulty = Difficulty.FACIL,
                        riddles = listOf(RIDDLE),
                        currentIndex = 0,
                        score = 2,
                        correctAnswers = 1,
                        selectedOption = 1,
                        isCorrect = true,
                        isLoading = false,
                    ),
                    onHintRequested = {},
                    onOptionSelected = {},
                    onNextQuestion = {},
                    onExit = {},
                )
            }
        }

        composeRule.onNodeWithText("¡Correcto! +2 puntos").assertIsDisplayed()
        composeRule.onNodeWithText("2 / 10 pts").assertIsDisplayed()
    }

    @Test
    fun juego_respuestaIncorrecta_suma0Puntos() {
        composeRule.setContent {
            AdivinaTITheme {
                GameContent(
                    state = GameUiState(
                        difficulty = Difficulty.FACIL,
                        riddles = listOf(RIDDLE),
                        currentIndex = 0,
                        score = 0,
                        correctAnswers = 0,
                        selectedOption = 0,
                        isCorrect = false,
                        isLoading = false,
                    ),
                    onHintRequested = {},
                    onOptionSelected = {},
                    onNextQuestion = {},
                    onExit = {},
                )
            }
        }

        composeRule.onNodeWithText("Incorrecto. +0 puntos").assertIsDisplayed()
        composeRule.onNodeWithText("0 / 10 pts").assertIsDisplayed()
    }

    @Test
    fun juego_ultimaPregunta_elBotonDiceVerResultado() {
        composeRule.setContent {
            AdivinaTITheme {
                GameContent(
                    state = GameUiState(
                        difficulty = Difficulty.FACIL,
                        riddles = List(5) { RIDDLE.copy(id = it + 1) },
                        currentIndex = 4,
                        isLoading = false,
                    ),
                    onHintRequested = {},
                    onOptionSelected = {},
                    onNextQuestion = {},
                    onExit = {},
                )
            }
        }

        composeRule.onNodeWithText("Pregunta 5 de 5").assertIsDisplayed()
        composeRule.onNodeWithText("Ver resultado").assertIsDisplayed().assertIsNotEnabled()
    }

    // ----------------------------------------------------------- Resultado

    @Test
    fun resultado_muestraPuntajeAciertosYDificultad() {
        composeRule.setContent {
            AdivinaTITheme {
                ResultContent(
                    state = ResultUiState(
                        result = GameResult(
                            id = 1_700_000_000_000L,
                            difficulty = Difficulty.MEDIA.key,
                            score = 8,
                            correctAnswers = 4,
                            date = "21/10/2026 14:32",
                        ),
                        difficulty = Difficulty.MEDIA,
                        isLoading = false,
                    ),
                    onBackToMenu = {},
                )
            }
        }

        composeRule.onNodeWithText("Resultado de la partida").assertIsDisplayed()
        composeRule.onNodeWithText("8 / 10").assertIsDisplayed()
        composeRule.onNodeWithText("Puntos").assertIsDisplayed()
        composeRule.onNodeWithText("Aciertos").assertIsDisplayed()
        composeRule.onNodeWithText("4 de 5").assertIsDisplayed()
        composeRule.onNodeWithText("Media").assertIsDisplayed()
        composeRule.onNodeWithText("80%").assertIsDisplayed()
        composeRule.onNodeWithText("Volver al menú").assertIsDisplayed().assertIsEnabled()
    }

    // ----------------------------------------------------------- Historial

    @Test
    fun historial_sinPartidas_muestraElEstadoVacio() {
        composeRule.setContent {
            AdivinaTITheme {
                HistoryContent(
                    state = HistoryUiState(isLoading = false),
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Historial de partidas").assertIsDisplayed()
        composeRule.onNodeWithText("Todavía no has jugado ninguna partida.", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Promedio: 0.0 pts").assertIsDisplayed()
    }

    @Test
    fun historial_conPartidas_muestraPromedioYMejorPuntuacion() {
        composeRule.setContent {
            AdivinaTITheme {
                HistoryContent(
                    state = HistoryUiState(
                        results = listOf(
                            GameResult(1L, Difficulty.FACIL.key, 8, 4, "21/10/2026 14:32"),
                            GameResult(2L, Difficulty.MEDIA.key, 6, 3, "21/10/2026 15:10"),
                        ),
                        averageScore = 7.0,
                        totalGames = 2,
                        bestScore = 8,
                        isLoading = false,
                    ),
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Promedio: 7.0 pts").assertIsDisplayed()
        composeRule.onNodeWithText("2 partidas · Mejor: 8/10").assertIsDisplayed()
        composeRule.onNodeWithText("8 / 10").assertIsDisplayed()
        composeRule.onNodeWithText("4 de 5 aciertos").assertIsDisplayed()
        composeRule.onNodeWithText("21/10/2026 14:32").assertIsDisplayed()
    }

    // ------------------------------------------------------ Nueva adivinanza

    @Test
    fun nuevaAdivinanza_muestraLosCuatroCamposYLasDificultades() {
        composeRule.setContent {
            AdivinaTITheme {
                AddRiddleContent(
                    state = AddRiddleUiState(),
                    onTextChange = {},
                    onHintChange = {},
                    onOptionChange = { _, _ -> },
                    onCorrectIndexSelected = {},
                    onDifficultySelected = {},
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Nueva adivinanza").assertIsDisplayed()
        composeRule.onNodeWithText("Enunciado").assertIsDisplayed()
        composeRule.onNodeWithText("Pista").assertIsDisplayed()
        composeRule.onNodeWithText("Opción A").assertIsDisplayed()
        composeRule.onNodeWithText("Opción B").assertIsDisplayed()
        composeRule.onNodeWithText("Opción C").assertIsDisplayed()
        composeRule.onNodeWithText("Opción D").assertIsDisplayed()
        composeRule.onNodeWithText("Dificultad").assertIsDisplayed()
        composeRule.onNodeWithText("Guardar adivinanza").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun nuevaAdivinanza_conErrores_deMuestraTodosLosMensajes() {
        composeRule.setContent {
            AdivinaTITheme {
                AddRiddleContent(
                    state = AddRiddleUiState(
                        textError = "Escribe el enunciado",
                        hintError = "Escribe una pista",
                        optionsError = "Las 4 opciones son obligatorias",
                        correctIndexError = "Selecciona la opción correcta",
                    ),
                    onTextChange = {},
                    onHintChange = {},
                    onOptionChange = { _, _ -> },
                    onCorrectIndexSelected = {},
                    onDifficultySelected = {},
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Escribe el enunciado").assertIsDisplayed()
        composeRule.onNodeWithText("Escribe una pista").assertIsDisplayed()
        composeRule.onNodeWithText("Las 4 opciones son obligatorias").assertIsDisplayed()
        composeRule.onNodeWithText("Selecciona la opción correcta").assertIsDisplayed()
    }

    @Test
    fun nuevaAdivinanza_alEscribir_yMandaLosCambiosAlPadre() {
        val state = mutableStateOf(AddRiddleUiState())
        composeRule.setContent {
            AdivinaTITheme {
                AddRiddleContent(
                    state = state.value,
                    onTextChange = { state.value = state.value.copy(text = it) },
                    onHintChange = { state.value = state.value.copy(hint = it) },
                    onOptionChange = { index, value ->
                        val updated = state.value.options.toMutableList()
                        updated[index] = value
                        state.value = state.value.copy(options = updated)
                    },
                    onCorrectIndexSelected = { state.value = state.value.copy(correctIndex = it) },
                    onDifficultySelected = { state.value = state.value.copy(difficulty = it) },
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Enunciado").performTextInput("¿Qué es un ViewModel?")
        composeRule.onNodeWithText("Pista").performTextInput("Es un patrón de diseño")
        composeRule.onNodeWithText("Opción A").performTextInput("Un patrón")
        composeRule.onNodeWithText("Opción B").performTextInput("Un widget")
        composeRule.onNodeWithText("Opción C").performTextInput("Una Activity")
        composeRule.onNodeWithText("Opción D").performTextInput("Un LiveData")

        composeRule.runOnIdle {
            assertEquals("¿Qué es un ViewModel?", state.value.text)
            assertEquals("Es un patrón de diseño", state.value.hint)
            assertEquals(listOf("Un patrón", "Un widget", "Una Activity", "Un LiveData"), state.value.options)
        }
    }

    private companion object {
        /** Adivinanza de ejemplo usada por los tests de la pantalla de juego. */
        val RIDDLE = Riddle(
            id = 1,
            text = "¿Qué lenguaje usa 'fun' para declarar funciones?",
            hint = "Android lo usa de forma nativa.",
            options = listOf("Java", "Kotlin", "Swift", "Dart"),
            correctIndex = 1,
            difficulty = Difficulty.FACIL.key,
            isUserCreated = false,
        )
    }
}
