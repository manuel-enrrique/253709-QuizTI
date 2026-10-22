@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.adivinati.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adivinati.core.GameRules
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult
import com.example.adivinati.ui.appViewModel
import com.example.adivinati.ui.theme.AdivinaTITheme

/**
 * Pantalla de resultado de la partida.
 *
 * Muestra el puntaje, los aciertos y permite volver al menú.
 * El guardado en el repositorio/DataStore ya se ha realizado en el
 * [com.example.adivinati.ui.game.GameViewModel].
 */
@Composable
fun ResultScreen(
    difficulty: Difficulty,
    onBackToMenu: () -> Unit,
) {
    val viewModel: ResultViewModel = appViewModel(key = "result_${difficulty.key}") { container ->
        ResultViewModel(difficulty = difficulty, repository = container.riddleRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ResultContent(state = state, onBackToMenu = onBackToMenu)
}

/** Contenido visual del resultado (sin estado propio). */
@Composable
fun ResultContent(
    state: ResultUiState,
    onBackToMenu: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Resultado de la partida") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator()
                return@Column
            }

            Text(
                text = when {
                    state.percentage >= 80 -> "¡Impresionante!"
                    state.percentage >= 50 -> "¡Bien hecho!"
                    else -> "¡Puedes mejorar!"
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "${state.score} / ${GameRules.MAX_SCORE}",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = "Puntos",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ResultRow(label = "Aciertos", value = "${state.correctAnswers} de ${GameRules.ROUND_SIZE}")
                    ResultRow(label = "Dificultad", value = state.difficulty.label)
                    ResultRow(label = "Fecha", value = state.date)
                    ResultRow(label = "Porcentaje", value = "${state.percentage}%")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(text = "Volver al menú", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** Fila etiqueta / valor del resumen de la partida. */
@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultContentPreview() {
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
