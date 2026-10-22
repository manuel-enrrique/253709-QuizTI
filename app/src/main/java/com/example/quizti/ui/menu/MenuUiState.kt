package com.example.quizti.ui.menu

data class MenuUiState(
    val availableCategories: List<String> = listOf(
        "Programación",
        "Redes",
        "Bases de datos",
        "Sistemas operativos"
    )
)
