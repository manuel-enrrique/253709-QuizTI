package com.example.quizti.data.model

/**
 * Modelo de pregunta para QuizTI.
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val category: String,
    val isUserCreated: Boolean
)
