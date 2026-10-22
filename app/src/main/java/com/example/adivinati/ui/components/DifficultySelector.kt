package com.example.adivinati.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.adivinati.data.model.Difficulty

/**
 * Selector de dificultad basado en `RadioButton`.
 *
 * Es un componente "tonto": no guarda estado, sólo refleja el valor recibido
 * y notifica los cambios (State Hoisting).
 */
@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == selectedDifficulty
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onDifficultySelected(difficulty) },
                    )
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null, // el click lo gestiona la Row seleccionable
                    enabled = enabled,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = difficulty.label,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
