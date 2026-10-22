package com.example.adivinati.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adivinati.core.DateFormatter
import com.example.adivinati.core.GameRules
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.data.model.GameResult
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
 * ViewModel de la pantalla de juego.
 *
 * Es el único que conoce las reglas de puntuación:
 *  - acertar sin pista = [GameRules.POINTS_WITHOUT_HINT] (2 puntos);
 *  - acertar con pista = [GameRules.POINTS_WITH_HINT] (1 punto);
 *  - fallar = [GameRules.POINTS_WRONG] (0 puntos).
 */
class GameViewModel(
    private val difficulty: Difficulty,
    private val repository: RiddleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(difficulty = difficulty))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Canal de eventos de una sola vez (navegación). */
    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val round = repository.getRound(difficulty)
            _uiState.update { it.copy(riddles = round, isLoading = false) }
        }
    }

    /** Evento de UI: el jugador pide una pista. */
    fun onHintRequested() {
        _uiState.update { it.copy(hintVisible = true) }
    }

    /**
     * Evento de UI: el jugador elige una opción.
     * Se ignora si ya respondió o si la ronda aún no ha cargado.
     */
    fun onOptionSelected(index: Int) {
        val state = _uiState.value
        if (state.isAnswered || state.isLoading) return
        val riddle = state.currentRiddle ?: return

        val isCorrect = index == riddle.correctIndex
        val points = GameRules.pointsFor(isCorrect = isCorrect, hintUsed = state.hintVisible)

        _uiState.update {
            it.copy(
                selectedOption = index,
                isCorrect = isCorrect,
                score = it.score + points,
                correctAnswers = it.correctAnswers + if (isCorrect) 1 else 0,
            )
        }
    }

    /**
     * Evento de UI: el jugador pulsa "Siguiente".
     * Si era la última pregunta, guarda el resultado y avisa con [GameEvent.Finished].
     */
    fun onNextQuestion() {
        val state = _uiState.value
        if (!state.isAnswered) return

        if (state.isLastQuestion) {
            viewModelScope.launch {
                val result = GameResult(
                    id = System.currentTimeMillis(),
                    difficulty = difficulty.key,
                    score = state.score,
                    correctAnswers = state.correctAnswers,
                    date = DateFormatter.now(),
                )
                repository.saveResult(result)
                _uiState.update { it.copy(isFinished = true) }
                _events.send(GameEvent.Finished)
            }
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    hintVisible = false,
                    selectedOption = null,
                    isCorrect = false,
                )
            }
        }
    }
}
