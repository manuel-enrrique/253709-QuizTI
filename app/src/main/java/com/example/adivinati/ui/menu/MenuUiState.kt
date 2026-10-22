package com.example.adivinati.ui.menu

import com.example.adivinati.data.model.Difficulty

/**
 * Estado inmutable de la pantalla de menú.
 *
 * Toda la información que la pantalla necesita viene de aquí: la UI es
 * Stateless y sólo refleja este estado.
 */
data class MenuUiState(
    /** Dificultad seleccionada por el usuario (se usará al pulsar "Jugar"). */
    val selectedDifficulty: Difficulty = Difficulty.FACIL,

    /** Número de adivinanzas disponibles para la dificultad seleccionada. */
    val availableRiddles: Int = 0,

    /** Número total de adivinanzas cargadas (semilla + creadas por el usuario). */
    val totalRiddles: Int = 0,

    /** `true` mientras se carga el JSON local de adivinanzas. */
    val isLoading: Boolean = true,
)
