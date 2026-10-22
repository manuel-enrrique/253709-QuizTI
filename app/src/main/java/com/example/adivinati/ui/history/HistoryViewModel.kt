package com.example.adivinati.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adivinati.data.repository.RiddleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de historial.
 *
 * Lee el historial persistido en DataStore a través del repositorio y calcula
 * las estadísticas (promedio, total de partidas y mejor puntuación).
 */
class HistoryViewModel(private val repository: RiddleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.history.collect { results ->
                val totalScore = results.sumOf { it.score }
                val average = if (results.isEmpty()) 0.0 else totalScore.toDouble() / results.size
                val best = results.maxOfOrNull { it.score } ?: 0
                _uiState.update {
                    it.copy(
                        results = results,
                        averageScore = average,
                        totalGames = results.size,
                        bestScore = best,
                        isLoading = false,
                    )
                }
            }
        }
    }
}
