package com.example.quizti

import android.app.Application
import com.example.quizti.core.AppContainer

class QuizTIApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
