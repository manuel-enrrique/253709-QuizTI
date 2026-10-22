package com.example.quizti.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizti.ui.addquestion.AddQuestionScreen
import com.example.quizti.ui.history.HistoryScreen
import com.example.quizti.ui.menu.MenuScreen
import com.example.quizti.ui.quiz.QuizScreen
import com.example.quizti.ui.result.ResultScreen

@Composable
fun QuizNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MENU
    ) {
        composable(Routes.MENU) {
            MenuScreen(
                onPlayCategory = { category -> navController.navigate(Routes.quiz(category)) },
                onAddQuestion = { navController.navigate(Routes.ADD_QUESTION) },
                onHistory = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument(Routes.ARG_CATEGORY) { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString(Routes.ARG_CATEGORY) ?: ""
            QuizScreen(
                category = category,
                onQuizFinished = { cat, score, total ->
                    navController.navigate(Routes.result(cat, score, total)) {
                        popUpTo(Routes.MENU)
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument(Routes.ARG_CATEGORY) { type = NavType.StringType },
                navArgument(Routes.ARG_SCORE) { type = NavType.IntType },
                navArgument(Routes.ARG_TOTAL) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString(Routes.ARG_CATEGORY) ?: ""
            val score = backStackEntry.arguments?.getInt(Routes.ARG_SCORE) ?: 0
            val total = backStackEntry.arguments?.getInt(Routes.ARG_TOTAL) ?: 0
            ResultScreen(
                category = category,
                score = score,
                total = total,
                onRepeat = { navController.navigate(Routes.quiz(category)) { popUpTo(Routes.MENU) } },
                onBackToMenu = { navController.popBackStack(Routes.MENU, inclusive = false) }
            )
        }

        composable(Routes.ADD_QUESTION) {
            AddQuestionScreen(
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
