package com.example.adivinati.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.Riddle
import com.example.adivinati.data.repository.RiddleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de menú.
 *
 * Aplica el flujo unidireccional: la UI envía eventos ([onDifficultySelected])
 * y el ViewModel actualiza un [StateFlow] inmutable que la UI recoge.
 */
class MenuViewModel(private val repository: RiddleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    /** Copia local de las adivinanzas para recalcular el contador de forma síncrona. */
    private var allRiddles: List<Riddle> = emptyList()

    /** `true` cuando el JSON local de semilla ya se ha cargado. */
    private var seedLoaded = false

    init {
        // Carga la semilla (simula la lectura del JSON local).
        viewModelScope.launch {
            repository.loadSeedRiddles()
            seedLoaded = true
            _uiState.update { it.copy(isLoading = false) }
        }

        // Observa las adivinanzas disponibles (semilla + las creadas por el usuario).
        viewModelScope.launch {
            repository.riddles.collect { riddles ->
                allRiddles = riddles
                _uiState.update { state ->
                    state.copy(
                        totalRiddles = riddles.size,
                        availableRiddles = riddles.count { it.difficulty == state.selectedDifficulty.key },
                        isLoading = !seedLoaded,
                    )
                }
            }
        }
    }

    /** Evento de UI: el usuario ha elegido una dificultad. */
    fun onDifficultySelected(difficulty: Difficulty) {
        _uiState.update { state ->
            state.copy(
                selectedDifficulty = difficulty,
                availableRiddles = allRiddles.count { it.difficulty == difficulty.key },
            )
        }
    }
}
