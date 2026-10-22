package com.example.adivinati.ui.addriddle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.repository.RiddleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del formulario de alta de adivinanzas.
 *
 * Valida los campos y persiste la adivinanza en DataStore a través del
 * repositorio (la UI nunca escribe directamente en el almacenamiento).
 */
class AddRiddleViewModel(private val repository: RiddleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRiddleUiState())
    val uiState: StateFlow<AddRiddleUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddRiddleEvent>(Channel.BUFFERED)
    val events: Flow<AddRiddleEvent> = _events.receiveAsFlow()

    /** Evento de UI: cambia el enunciado. */
    fun onTextChange(value: String) {
        _uiState.update { it.copy(text = value, textError = null) }
    }

    /** Evento de UI: cambia la pista. */
    fun onHintChange(value: String) {
        _uiState.update { it.copy(hint = value, hintError = null) }
    }

    /** Evento de UI: cambia el texto de una de las opciones. */
    fun onOptionChange(index: Int, value: String) {
        _uiState.update { state ->
            val updated = state.options.toMutableList()
            if (index in updated.indices) updated[index] = value
            state.copy(options = updated, optionsError = null)
        }
    }

    /** Evento de UI: se marca una opción como correcta. */
    fun onCorrectIndexSelected(index: Int) {
        _uiState.update { it.copy(correctIndex = index, correctIndexError = null) }
    }

    /** Evento de UI: se cambia la dificultad. */
    fun onDifficultySelected(difficulty: Difficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    /**
     * Evento de UI: se pulsa "Guardar".
     *
     * Primero valida; si algo falla, actualiza el estado con los errores y no
     * escribe nada. Si todo es correcto, persiste la adivinanza.
     */
    fun onSave() {
        val state = _uiState.value
        val text = state.text.trim()
        val hint = state.hint.trim()
        val options = state.options.map { it.trim() }

        val textError = if (text.isEmpty()) "Escribe el enunciado" else null
        val hintError = if (hint.isEmpty()) "Escribe una pista" else null
        val optionsError = if (options.any { it.isEmpty() }) {
            "Las 4 opciones son obligatorias"
        } else {
            null
        }
        val correctIndexError = if (state.correctIndex == null) {
            "Selecciona la opción correcta"
        } else {
            null
        }

        if (textError != null || hintError != null || optionsError != null || correctIndexError != null) {
            _uiState.update {
                it.copy(
                    textError = textError,
                    hintError = hintError,
                    optionsError = optionsError,
                    correctIndexError = correctIndexError,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.addUserRiddle(
                text = text,
                hint = hint,
                options = options,
                correctIndex = state.correctIndex ?: 0,
                difficulty = state.difficulty,
            )
            _uiState.update { it.copy(isSaving = false, saved = true) }
            _events.send(AddRiddleEvent.Saved)
        }
    }
}
