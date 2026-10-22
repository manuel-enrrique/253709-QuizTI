package com.example.quizti.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quizti.ui.appViewModel

@Composable
fun QuizScreen(
    category: String,
    onQuizFinished: (String, Int, Int) -> Unit,
    onExit: () -> Unit
) {
    val viewModel: QuizViewModel = appViewModel(key = category) { container ->
        QuizViewModel(container.questionRepository, category)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onQuizFinished(category, state.correctAnswers, state.questions.size)
        }
    }

    QuizContent(
        state = state,
        onSelectOption = viewModel::selectOption,
        onConfirm = viewModel::confirmAnswer,
        onNext = viewModel::nextQuestion
    )
}

@Composable
fun QuizContent(
    state: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit
) {
    if (state.questions.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Cargando preguntas...")
        }
        return
    }

    val currentQuestion = state.questions[state.currentIndex]
    val progress = (state.currentIndex + 1).toFloat() / state.questions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pregunta ${state.currentIndex + 1} de ${state.questions.size}",
            style = MaterialTheme.typography.titleMedium
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = currentQuestion.text,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        currentQuestion.options.forEachIndexed { index, option ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = state.selectedOptionIndex == index,
                        enabled = !state.isConfirmed,
                        onClick = { onSelectOption(index) }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = state.selectedOptionIndex == index,
                        onClick = null,
                        enabled = !state.isConfirmed
                    )
                    Text(text = option, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!state.isConfirmed) {
            Button(
                onClick = onConfirm,
                enabled = state.selectedOptionIndex != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "Confirmar respuesta", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (state.currentIndex + 1 < state.questions.size) "Siguiente" else "Ver resultado",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
