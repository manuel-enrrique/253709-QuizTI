package com.example.quizti.core

import android.content.Context
import com.example.quizti.data.local.QuestionsDataSource
import com.example.quizti.data.local.QuizResultDataSource
import com.example.quizti.data.local.UserQuestionsDataSource
import com.example.quizti.data.local.quizDataStore
import com.example.quizti.data.repository.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {
    private val appContext: Context = context.applicationContext
    private val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val dataStore = appContext.quizDataStore

    private val questionsDataSource = QuestionsDataSource(appContext)
    private val userQuestionsDataSource = UserQuestionsDataSource(dataStore)
    private val quizResultDataSource = QuizResultDataSource(dataStore)

    val questionRepository: QuestionRepository = QuestionRepository(
        questionsDataSource = questionsDataSource,
        userQuestionsDataSource = userQuestionsDataSource,
        quizResultDataSource = quizResultDataSource,
        scope = appScope
    )
}
