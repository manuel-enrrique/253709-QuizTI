package com.example.quizti.ui.quiz

import com.example.quizti.data.model.Question

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isConfirmed: Boolean = false,
    val correctAnswers: Int = 0,
    val isFinished: Boolean = false
)
