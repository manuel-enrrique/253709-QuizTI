package com.example.adivinati.data.model

/**
 * Resultado de una partida terminada. Se persiste en DataStore como historial.
 *
 * @param id             Timestamp de la partida (en milisegundos). Sirve como clave primaria
 *                       y como criterio de orden (más reciente = mayor id).
 * @param difficulty     Clave de dificultad con la que se jugó la partida.
 * @param score          Puntos conseguidos (máximo 10 por ronda de 5).
 * @param correctAnswers Número de respuestas acertadas de la ronda.
 * @param date           Fecha ya formateada para mostrar ("21/10/2026 14:32").
 */
data class GameResult(
    val id: Long,
    val difficulty: String,
    val score: Int,
    val correctAnswers: Int,
    val date: String,
)
