package com.example.quizti.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizti.data.model.QuizResult
import com.example.quizti.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizViewModel(
    private val repository: QuestionRepository,
    private val category: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = repository.getQuestionsByCategory(category)
            _uiState.update { it.copy(questions = questions) }
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isConfirmed) return
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun confirmAnswer() {
        val state = _uiState.value
        if (state.selectedOptionIndex == null || state.isConfirmed) return
        val currentQuestion = state.questions[state.currentIndex]
        val isCorrect = state.selectedOptionIndex == currentQuestion.correctIndex
        val newCorrect = if (isCorrect) state.correctAnswers + 1 else state.correctAnswers
        _uiState.update {
            it.copy(
                correctAnswers = newCorrect,
                isConfirmed = true
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentIndex + 1 < state.questions.size) {
            _uiState.update {
                it.copy(
                    currentIndex = state.currentIndex + 1,
                    selectedOptionIndex = null,
                    isConfirmed = false
                )
            }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val state = _uiState.value
        _uiState.update { it.copy(isFinished = true) }
        viewModelScope.launch {
            val result = QuizResult(
                id = System.currentTimeMillis(),
                category = category,
                score = state.correctAnswers,
                total = state.questions.size,
                date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )
            repository.saveResult(result)
        }
    }
}
