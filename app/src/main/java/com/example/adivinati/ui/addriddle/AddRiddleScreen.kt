@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.adivinati.ui.addriddle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.ui.appViewModel
import com.example.adivinati.ui.components.DifficultySelector
import com.example.adivinati.ui.theme.AdivinaTITheme

/**
 * Formulario para crear una adivinanza nueva.
 *
 * La validación vive en el [AddRiddleViewModel]; la pantalla sólo muestra los
 * errores que llegan en el estado y lanza los eventos de edición.
 */
@Composable
fun AddRiddleScreen(onSaved: () -> Unit) {
    val viewModel: AddRiddleViewModel = appViewModel { container ->
        AddRiddleViewModel(repository = container.riddleRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                AddRiddleEvent.Saved -> onSaved()
            }
        }
    }

    AddRiddleContent(
        state = state,
        onTextChange = viewModel::onTextChange,
        onHintChange = viewModel::onHintChange,
        onOptionChange = viewModel::onOptionChange,
        onCorrectIndexSelected = viewModel::onCorrectIndexSelected,
        onDifficultySelected = viewModel::onDifficultySelected,
        onSave = viewModel::onSave,
    )
}

/** Contenido visual del formulario (sin estado propio). */
@Composable
fun AddRiddleContent(
    state: AddRiddleUiState,
    onTextChange: (String) -> Unit,
    onHintChange: (String) -> Unit,
    onOptionChange: (Int, String) -> Unit,
    onCorrectIndexSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Nueva adivinanza") })
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
            OutlinedTextField(
                value = state.text,
                onValueChange = onTextChange,
                label = { Text(text = "Enunciado") },
                placeholder = { Text(text = "¿Qué lenguaje usa 'fun' para declarar funciones?") },
                isError = state.textError != null,
                supportingText = if (state.textError != null) {
                    { Text(text = state.textError) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
            )

            OutlinedTextField(
                value = state.hint,
                onValueChange = onHintChange,
                label = { Text(text = "Pista") },
                placeholder = { Text(text = "Android lo usa de forma nativa") },
                isError = state.hintError != null,
                supportingText = if (state.hintError != null) {
                    { Text(text = state.hintError) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
            )

            Text(
                text = "Opciones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = "Marca con el círculo la opción correcta.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            state.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = state.correctIndex == index,
                        onClick = { onCorrectIndexSelected(index) },
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = option,
                        onValueChange = { onOptionChange(index, it) },
                        label = { Text(text = "Opción ${('A'.code + index).toChar()}") },
                        isError = state.optionsError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            }

            if (state.optionsError != null) {
                Text(
                    text = state.optionsError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (state.correctIndexError != null) {
                Text(
                    text = state.correctIndexError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = "Dificultad",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            DifficultySelector(
                selectedDifficulty = state.difficulty,
                onDifficultySelected = onDifficultySelected,
                enabled = !state.isSaving,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !state.isSaving,
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Guardar adivinanza", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddRiddleContentPreview() {
    AdivinaTITheme {
        AddRiddleContent(
            state = AddRiddleUiState(
                text = "¿Qué lenguaje usa 'fun' para declarar funciones?",
                hint = "Android lo usa de forma nativa",
                options = listOf("Java", "Kotlin", "Swift", "Dart"),
                correctIndex = 1,
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
