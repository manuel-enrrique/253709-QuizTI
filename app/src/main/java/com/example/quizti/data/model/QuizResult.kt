package com.example.quizti.data.model

/**
 * Resultado de un quiz guardado en el historial.
 */
data class QuizResult(
    val id: Long,
    val category: String,
    val score: Int,
    val total: Int,
    val date: String
)
