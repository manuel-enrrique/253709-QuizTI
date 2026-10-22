package com.example.quizti.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.quizti.core.AppContainer

val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("LocalAppContainer no está proporcionado")
}

@Composable
inline fun <reified VM : ViewModel> appViewModel(
    key: String? = null,
    crossinline creator: (AppContainer) -> VM,
): VM {
    val container = LocalAppContainer.current
    val factory = remember(container, key) {
        viewModelFactory {
            initializer { creator(container) }
        }
    }
    return viewModel(key = key, factory = factory)
}
