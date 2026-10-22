package com.example.adivinati.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.repository.RiddleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de resultado.
 *
 * El [com.example.adivinati.ui.game.GameViewModel] ya guardó el `GameResult`
 * en el repositorio (y por tanto en DataStore) antes de navegar hasta aquí;
 * este ViewModel simplemente lee el último resultado de esa dificultad.
 */
class ResultViewModel(
    private val difficulty: Difficulty,
    private val repository: RiddleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState(difficulty = difficulty))
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.history.collect { results ->
                val lastResult = results.firstOrNull { it.difficulty == difficulty.key }
                _uiState.update { it.copy(result = lastResult, isLoading = false) }
            }
        }
    }
}
