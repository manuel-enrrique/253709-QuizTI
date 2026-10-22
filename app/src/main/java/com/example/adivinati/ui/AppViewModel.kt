package com.example.adivinati.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.adivinati.core.AppContainer

/**
 * Acceso al [AppContainer] desde cualquier Composable.
 *
 * Se provee en `MainActivity` con `CompositionLocalProvider`, de forma que las
 * pantallas (y sus ViewModels) reciben el repositorio sin conocer la Activity.
 */
val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("LocalAppContainer no está proporcionado. Envuélvelo en CompositionLocalProvider.")
}

/**
 * Crea (o recupera) un ViewModel de la app con una factoría manual.
 *
 * @param key     Clave opcional para distinguir instancias del mismo ViewModel.
 * @param creator Función que construye el ViewModel a partir del contenedor.
 */
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
