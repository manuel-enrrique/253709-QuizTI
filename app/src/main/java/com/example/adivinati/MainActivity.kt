package com.example.adivinati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.adivinati.ui.LocalAppContainer
import com.example.adivinati.ui.navigation.AdivinaNavHost
import com.example.adivinati.ui.theme.AdivinaTITheme

/**
 * Única Activity de la app.
 *
 * La app es 100% offline: no hay permisos de red ni llamadas a servicios
 * remotos. Toda la persistencia es local (Preferences DataStore).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as AdivinaTIApplication).container

        setContent {
            AdivinaTITheme {
                // Se comparte el contenedor (DataStore + repositorio) con toda la UI.
                CompositionLocalProvider(LocalAppContainer provides container) {
                    AdivinaNavHost()
                }
            }
        }
    }
}
