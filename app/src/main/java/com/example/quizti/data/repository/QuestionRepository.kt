package com.example.quizti.data.repository

import com.example.quizti.data.local.QuestionsDataSource
import com.example.quizti.data.local.QuizResultDataSource
import com.example.quizti.data.local.UserQuestionsDataSource
import com.example.quizti.data.model.Question
import com.example.quizti.data.model.QuizResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class QuestionRepository(
    private val questionsDataSource: QuestionsDataSource,
    private val userQuestionsDataSource: UserQuestionsDataSource,
    private val quizResultDataSource: QuizResultDataSource,
    scope: CoroutineScope
) {
    private val _seedQuestions = MutableStateFlow<List<Question>>(emptyList())

    val questions: StateFlow<List<Question>> = combine(
        _seedQuestions.asStateFlow(),
        userQuestionsDataSource.userQuestions
    ) { seed, user ->
        seed + user
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val history: StateFlow<List<QuizResult>> = quizResultDataSource.quizResults
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    suspend fun loadSeedQuestions(): List<Question> {
        if (_seedQuestions.value.isNotEmpty()) return _seedQuestions.value
        val loaded = questionsDataSource.loadQuestionsFromRaw()
        _seedQuestions.value = loaded
        return loaded
    }

    suspend fun getQuestionsByCategory(category: String): List<Question> {
        loadSeedQuestions()
        return questions.value.filter { it.category == category }.shuffled().take(5)
    }

    suspend fun addUserQuestion(
        text: String,
        options: List<String>,
        correctIndex: Int,
        category: String
    ): Question {
        val nextId = (questions.value.maxOfOrNull { it.id } ?: 0) + 1
        val question = Question(
            id = nextId,
            text = text,
            options = options,
            correctIndex = correctIndex,
            category = category,
            isUserCreated = true
        )
        userQuestionsDataSource.addQuestion(question)
        return question
    }

    suspend fun saveResult(result: QuizResult) {
        quizResultDataSource.saveResult(result)
    }
}
