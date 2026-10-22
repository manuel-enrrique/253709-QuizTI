@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.adivinati.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adivinati.core.GameRules
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.Riddle
import com.example.adivinati.ui.appViewModel
import com.example.adivinati.ui.components.OptionButton
import com.example.adivinati.ui.theme.AdivinaTITheme

/**
 * Pantalla de juego: muestra una ronda de 5 adivinanzas, una a una.
 *
 * La pantalla es "stateless": el estado vive en [GameViewModel] y aquí sólo se
 * dibuja y se envían eventos (State Hoisting).
 */
@Composable
fun GameScreen(
    difficulty: Difficulty,
    onGameFinished: () -> Unit,
    onExit: () -> Unit,
) {
    val viewModel: GameViewModel = appViewModel(key = "game_${difficulty.key}") { container ->
        GameViewModel(difficulty = difficulty, repository = container.riddleRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Evento de una sola vez: navegar a la pantalla de resultado.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent.Finished -> onGameFinished()
            }
        }
    }

    GameContent(
        state = state,
        onHintRequested = viewModel::onHintRequested,
        onOptionSelected = viewModel::onOptionSelected,
        onNextQuestion = viewModel::onNextQuestion,
        onExit = onExit,
    )
}

/** Contenido visual del juego, sin estado propio (permite previsualizarlo). */
@Composable
fun GameContent(
    state: GameUiState,
    onHintRequested: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onExit: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "AdivinaTI · ${state.difficulty.label}") },
                actions = {
                    IconButton(onClick = onExit) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Salir de la partida")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            val riddle = state.currentRiddle ?: return@Column

            // --- Cabecera: progreso y puntuación ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Pregunta ${state.currentIndex + 1} de ${state.totalQuestions}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Text(
                        text = "${state.score} / ${GameRules.MAX_SCORE} pts",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.totalQuestions.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth(),
            )

            // --- Enunciado ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = riddle.text,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp),
                )
            }

            // --- Pista ---
            if (state.hintVisible) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                        Column {
                            Text(
                                text = "Pista",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(text = riddle.hint, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onHintRequested,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Pedir pista (acierto = 1 punto)")
                }
            }

            // --- Opciones ---
            riddle.options.forEachIndexed { index, option ->
                OptionButton(
                    text = option,
                    index = index,
                    isSelected = state.selectedOption == index,
                    isCorrectAnswer = index == riddle.correctIndex,
                    isAnswered = state.isAnswered,
                    onClick = { onOptionSelected(index) },
                )
            }

            // --- Feedback ---
            if (state.isAnswered) {
                FeedbackBanner(isCorrect = state.isCorrect, points = state.lastAnswerPoints)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- Acción principal ---
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.isAnswered,
            ) {
                Text(
                    text = if (state.isLastQuestion) "Ver resultado" else "Siguiente",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Banner de feedback tras responder: indica si se acertó y cuántos puntos se suman. */
@Composable
private fun FeedbackBanner(isCorrect: Boolean, points: Int) {
    val containerColor = if (isCorrect) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isCorrect) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = if (isCorrect) {
                "¡Correcto! +$points puntos"
            } else {
                "Incorrecto. +$points puntos"
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameContentPreview() {
    val riddle = Riddle(
        id = 1,
        text = "Soy un lenguaje de programación moderno que usa \"fun\" para declarar funciones.",
        hint = "Android lo usa de forma nativa.",
        options = listOf("Java", "Kotlin", "Swift", "Dart"),
        correctIndex = 1,
        difficulty = Difficulty.FACIL.key,
        isUserCreated = false,
    )
    AdivinaTITheme {
        GameContent(
            state = GameUiState(
                difficulty = Difficulty.FACIL,
                riddles = listOf(riddle),
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
