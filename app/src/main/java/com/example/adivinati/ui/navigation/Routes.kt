package com.example.adivinati.ui.navigation

import com.example.adivinati.data.model.Difficulty

/**
 * Rutas del grafo de navegación.
 *
 * La dificultad viaja como argumento de ruta usando su clave estable
 * ("FACIL", "MEDIA", "DIFICIL"), nunca su etiqueta con acentos.
 */
object Routes {

    const val ARG_DIFFICULTY = "difficulty"

    const val MENU = "menu"
    const val GAME = "game/{$ARG_DIFFICULTY}"
    const val RESULT = "result/{$ARG_DIFFICULTY}"
    const val ADD_RIDDLE = "add_riddle"
    const val HISTORY = "history"

    fun game(difficulty: Difficulty): String = "game/${difficulty.key}"

    fun result(difficulty: Difficulty): String = "result/${difficulty.key}"
}
