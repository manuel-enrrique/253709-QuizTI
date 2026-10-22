package com.example.quizti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.quizti.ui.LocalAppContainer
import com.example.quizti.ui.navigation.QuizNavHost
import com.example.quizti.ui.theme.QuizTITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as QuizTIApplication).container
        setContent {
            QuizTITheme {
                CompositionLocalProvider(LocalAppContainer provides container) {
                    QuizNavHost()
                }
            }
        }
    }
}
