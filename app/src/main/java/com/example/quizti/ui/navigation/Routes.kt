package com.example.quizti.ui.navigation

object Routes {
    const val ARG_CATEGORY = "category"
    const val ARG_SCORE = "score"
    const val ARG_TOTAL = "total"

    const val MENU = "menu"
    const val QUIZ = "quiz/{$ARG_CATEGORY}"
    const val RESULT = "result/{$ARG_CATEGORY}/{$ARG_SCORE}/{$ARG_TOTAL}"
    const val ADD_QUESTION = "add_question"
    const val HISTORY = "history"

    fun quiz(category: String): String = "quiz/$category"
    fun result(category: String, score: Int, total: Int): String = "result/$category/$score/$total"
}
