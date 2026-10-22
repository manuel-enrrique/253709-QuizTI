package com.example.adivinati.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.adivinati.data.local.GameResultDataSource
import com.example.adivinati.data.local.SeedRiddlesDataSource
import com.example.adivinati.data.local.UserRiddlesDataSource
import com.example.adivinati.data.repository.RiddleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Contenedor de dependencias de la app (inyección de dependencias manual).
 *
 * Se crea una única vez en [com.example.adivinati.AdivinaTIApplication] y se
 * pasa a la capa de presentación mediante `LocalAppContainer`. Así, sin Hilt ni
 * KSP, cada ViewModel recibe el repositorio ya construido.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    /** Scope interno del repositorio, ligado al ciclo de vida de la aplicación. */
    private val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Instancia única de Preferences DataStore.
     *
     * Importante: DataStore debe crearse una sola vez por fichero, por eso vive
     * en el contenedor y no en los data sources.
     */
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { appContext.preferencesDataStoreFile(PREFERENCES_FILE_NAME) },
    )

    // --- Data sources ---
    private val seedRiddlesDataSource = SeedRiddlesDataSource(appContext)
    private val userRiddlesDataSource = UserRiddlesDataSource(dataStore)
    private val gameResultDataSource = GameResultDataSource(dataStore)

    // --- Repositorio (única puerta de entrada a los datos) ---
    val riddleRepository: RiddleRepository = RiddleRepository(
        seedDataSource = seedRiddlesDataSource,
        userRiddlesDataSource = userRiddlesDataSource,
        gameResultDataSource = gameResultDataSource,
        scope = appScope,
    )

    private companion object {
        const val PREFERENCES_FILE_NAME = "adivinati_prefs"
    }
}
