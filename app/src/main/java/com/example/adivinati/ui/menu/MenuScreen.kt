package com.example.adivinati.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.ui.appViewModel
import com.example.adivinati.ui.components.DifficultySelector
import com.example.adivinati.ui.theme.AdivinaTITheme

/**
 * Pantalla principal de AdivinaTI.
 *
 * Es un Composable "stateless": no guarda estado propio, recibe los callbacks
 * de navegación y delega el estado en [MenuViewModel] (State Hoisting).
 */
@Composable
fun MenuScreen(
    onPlay: (Difficulty) -> Unit,
    onAddRiddle: () -> Unit,
    onHistory: () -> Unit,
) {
    val viewModel: MenuViewModel = appViewModel { container -> MenuViewModel(container.riddleRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onDifficultySelected = viewModel::onDifficultySelected,
        onPlay = { onPlay(state.selectedDifficulty) },
        onAddRiddle = onAddRiddle,
        onHistory = onHistory,
    )
}

/**
 * Contenido visual del menú. Es completamente tonto: sólo recibe el estado
 * y lanza eventos hacia arriba, lo que permite previsualizarlo sin ViewModel.
 */
@Composable
fun MenuContent(
    state: MenuUiState,
    onDifficultySelected: (Difficulty) -> Unit,
    onPlay: () -> Unit,
    onAddRiddle: () -> Unit,
    onHistory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "AdivinaTI",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Pon a prueba tus conocimientos de tecnología",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dificultad",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                DifficultySelector(
                    selectedDifficulty = state.selectedDifficulty,
                    onDifficultySelected = onDifficultySelected,
                    enabled = !state.isLoading,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (state.isLoading) {
                        "Cargando adivinanzas..."
                    } else {
                        "${state.availableRiddles} adivinanzas disponibles · ${state.totalRiddles} en total"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Button(
            onClick = onPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !state.isLoading && state.availableRiddles > 0,
        ) {
            Text(text = "Jugar", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = onAddRiddle,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(text = "Agregar Adivinanza", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = onHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(text = "Ver Historial", style = MaterialTheme.typography.titleMedium)
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Text(
            text = "Cada ronda son 5 preguntas. Acierta sin pista para sumar 2 puntos " +
                "y con pista para sumar 1 punto. Máximo: 10 puntos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuContentPreview() {
    AdivinaTITheme {
        MenuContent(
            state = MenuUiState(availableRiddles = 5, totalRiddles = 15, isLoading = false),
            onDifficultySelected = {},
            onPlay = {},
            onAddRiddle = {},
            onHistory = {},
        )
    }
}
