package com.example.adivinati.ui.addriddle

import com.example.adivinati.data.model.Difficulty

/**
 * Estado inmutable del formulario de alta de adivinanzas.
 *
 * Los errores de validación viven en el estado (no en la UI) para que la
 * pantalla sea un Composable tonto.
 */
data class AddRiddleUiState(
    /** Enunciado de la adivinanza. */
    val text: String = "",

    /** Pista que se mostrará al jugador. */
    val hint: String = "",

    /** Las 4 opciones de respuesta. */
    val options: List<String> = listOf("", "", "", ""),

    /** Índice de la opción correcta, o `null` si el usuario no ha elegido ninguna. */
    val correctIndex: Int? = null,

    /** Dificultad de la adivinanza. */
    val difficulty: Difficulty = Difficulty.FACIL,

    /** Error del enunciado (null si es válido). */
    val textError: String? = null,

    /** Error de la pista (null si es válida). */
    val hintError: String? = null,

    /** Error de las opciones (null si son válidas). */
    val optionsError: String? = null,

    /** Error del selector de opción correcta (null si es válido). */
    val correctIndexError: String? = null,

    /** `true` mientras se guarda en DataStore. */
    val isSaving: Boolean = false,

    /** `true` cuando la adivinanza ya se ha guardado correctamente. */
    val saved: Boolean = false,
) {
    /** Número de opciones del formulario (siempre 4). */
    val optionCount: Int get() = options.size
}

/** Eventos de una sola vez emitidos por el [AddRiddleViewModel]. */
sealed interface AddRiddleEvent {
    /** La adivinanza se ha guardado: hay que volver al menú. */
    data object Saved : AddRiddleEvent
}
