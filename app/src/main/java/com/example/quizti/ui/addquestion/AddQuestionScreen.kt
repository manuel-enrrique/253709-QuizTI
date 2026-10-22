package com.example.quizti.ui.addquestion

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quizti.ui.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    onSaved: () -> Unit
) {
    val viewModel: AddQuestionViewModel = appViewModel { container ->
        AddQuestionViewModel(container.questionRepository)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Agregar pregunta",
            style = MaterialTheme.typography.headlineMedium
        )

        if (state.errors.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.errors.forEach { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.text,
            onValueChange = viewModel::updateText,
            label = { Text("Texto de la pregunta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = state.option1,
            onValueChange = viewModel::updateOption1,
            label = { Text("Opción 1") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.option2,
            onValueChange = viewModel::updateOption2,
            label = { Text("Opción 2") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.option3,
            onValueChange = viewModel::updateOption3,
            label = { Text("Opción 3") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.option4,
            onValueChange = viewModel::updateOption4,
            label = { Text("Opción 4") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Selecciona la respuesta correcta",
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = state.correctIndex == 0, onClick = { viewModel.updateCorrectIndex(0) })
            Text("Opción 1")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = state.correctIndex == 1, onClick = { viewModel.updateCorrectIndex(1) })
            Text("Opción 2")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = state.correctIndex == 2, onClick = { viewModel.updateCorrectIndex(2) })
            Text("Opción 3")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = state.correctIndex == 3, onClick = { viewModel.updateCorrectIndex(3) })
            Text("Opción 4")
        }

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = state.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            viewModel.updateCategory(category)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = viewModel::saveQuestion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = "Guardar pregunta", style = MaterialTheme.typography.titleMedium)
        }
    }
}
