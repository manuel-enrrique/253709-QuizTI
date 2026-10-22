package com.example.quizti.ui.addquestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizti.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddQuestionViewModel(private val repository: QuestionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddQuestionUiState())
    val uiState: StateFlow<AddQuestionUiState> = _uiState.asStateFlow()

    val categories = listOf("Programación", "Redes", "Bases de datos", "Sistemas operativos")

    fun updateText(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun updateOption1(option: String) {
        _uiState.update { it.copy(option1 = option) }
    }

    fun updateOption2(option: String) {
        _uiState.update { it.copy(option2 = option) }
    }

    fun updateOption3(option: String) {
        _uiState.update { it.copy(option3 = option) }
    }

    fun updateOption4(option: String) {
        _uiState.update { it.copy(option4 = option) }
    }

    fun updateCorrectIndex(index: Int) {
        _uiState.update { it.copy(correctIndex = index) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun saveQuestion() {
        val state = _uiState.value
        val errors = mutableListOf<String>()

        if (state.text.isBlank()) errors.add("El texto de la pregunta es obligatorio")
        if (state.option1.isBlank()) errors.add("La opción 1 es obligatoria")
        if (state.option2.isBlank()) errors.add("La opción 2 es obligatoria")
        if (state.option3.isBlank()) errors.add("La opción 3 es obligatoria")
        if (state.option4.isBlank()) errors.add("La opción 4 es obligatoria")

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        val options = listOf(state.option1, state.option2, state.option3, state.option4)
        viewModelScope.launch {
            repository.addUserQuestion(
                text = state.text,
                options = options,
                correctIndex = state.correctIndex,
                category = state.category
            )
            _uiState.update { it.copy(isSaved = true, errors = emptyList()) }
        }
    }
}
