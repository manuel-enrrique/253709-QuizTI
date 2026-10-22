package com.example.adivinati.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.adivinati.data.model.Difficulty
import com.example.adivinati.ui.addriddle.AddRiddleScreen
import com.example.adivinati.ui.game.GameScreen
import com.example.adivinati.ui.history.HistoryScreen
import com.example.adivinati.ui.menu.MenuScreen
import com.example.adivinati.ui.result.ResultScreen

/**
 * Grafo de navegación de AdivinaTI.
 *
 * ```
 * menu ──(dificultad)──> game ──(fin de ronda)──> result
 *   ├──> add_riddle
 *   └──> history
 * ```
 */
@Composable
fun AdivinaNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MENU,
    ) {
        // ---------------------------------------------------------------- Menú
        composable(Routes.MENU) {
            MenuScreen(
                onPlay = { difficulty -> navController.navigate(Routes.game(difficulty)) },
                onAddRiddle = { navController.navigate(Routes.ADD_RIDDLE) },
                onHistory = { navController.navigate(Routes.HISTORY) },
            )
        }

        // ------------------------------------------------------------ Partida
        composable(
            route = Routes.GAME,
            arguments = listOf(
                navArgument(Routes.ARG_DIFFICULTY) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val difficulty = Difficulty.fromKey(
                backStackEntry.arguments?.getString(Routes.ARG_DIFFICULTY),
            )
            GameScreen(
                difficulty = difficulty,
                onGameFinished = {
                    // Se limpia la pila de la partida: al volver se llega al menú.
                    navController.navigate(Routes.result(difficulty)) {
                        popUpTo(Routes.MENU)
                    }
                },
                onExit = { navController.popBackStack() },
            )
        }

        // ----------------------------------------------------------- Resultado
        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument(Routes.ARG_DIFFICULTY) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val difficulty = Difficulty.fromKey(
                backStackEntry.arguments?.getString(Routes.ARG_DIFFICULTY),
            )
            ResultScreen(
                difficulty = difficulty,
                onBackToMenu = { navController.popBackStack(Routes.MENU, inclusive = false) },
            )
        }

        // ------------------------------------------------- Nueva adivinanza
        composable(Routes.ADD_RIDDLE) {
            AddRiddleScreen(
                onSaved = { navController.popBackStack() },
            )
        }

        // ------------------------------------------------------------ Historial
        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
