package com.example.quizti.ui.history

import com.example.quizti.data.model.QuizResult

data class HistoryUiState(
    val results: List<QuizResult> = emptyList()
)
