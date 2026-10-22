package com.example.quizti.ui.addquestion

data class AddQuestionUiState(
    val text: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val correctIndex: Int = 0,
    val category: String = "Programación",
    val errors: List<String> = emptyList(),
    val isSaved: Boolean = false
)
