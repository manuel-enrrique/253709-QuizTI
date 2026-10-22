@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.adivinati.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Pantalla de historial de partidas.
 *
 * Muestra la lista de partidas guardadas en DataStore y el promedio de puntos.
 */
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val viewModel: HistoryViewModel = appViewModel { container ->
        HistoryViewModel(repository = container.riddleRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(state = state, onBack = onBack)
}

/** Contenido visual del historial (sin estado propio). */
@Composable
fun HistoryContent(
    state: HistoryUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Historial de partidas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // --- Resumen: partidas jugadas, promedio y mejor puntuación ---
            item(key = "summary") {
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Promedio: ${"%.1f".format(state.averageScore)} pts",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "${state.totalGames} partidas · Mejor: ${state.bestScore}/${GameRules.MAX_SCORE}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            if (state.isEmpty) {
                item(key = "empty") {
                    Text(
                        text = "Todavía no has jugado ninguna partida.\n¡Vuelve al menú y empieza a jugar!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                    )
                }
            } else {
                items(state.results, key = { it.id }) { result ->
                    GameResultCard(result = result)
                }
            }
        }
    }
}

/** Tarjeta con los datos de una partida. */
@Composable
private fun GameResultCard(result: GameResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = Difficulty.fromKey(result.difficulty).label,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${result.score} / ${GameRules.MAX_SCORE}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${result.correctAnswers} de ${GameRules.ROUND_SIZE} aciertos",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = result.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryContentPreview() {
    AdivinaTITheme {
        HistoryContent(
            state = HistoryUiState(
                results = listOf(
                    GameResult(1_700_000_000_000L, Difficulty.FACIL.key, 8, 4, "21/10/2026 14:32"),
                    GameResult(1_700_000_100_000L, Difficulty.MEDIA.key, 6, 3, "21/10/2026 15:10"),
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
