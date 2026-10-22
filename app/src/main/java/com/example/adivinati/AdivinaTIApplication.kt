package com.example.adivinati

import android.app.Application
import com.example.adivinati.core.AppContainer

/**
 * Application de AdivinaTI.
 *
 * Crea el [AppContainer] (con el DataStore y el repositorio) y lo mantiene
 * vivo durante toda la vida de la app.
 */
class AdivinaTIApplication : Application() {

    /** Contenedor de dependencias compartido por toda la aplicación. */
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
