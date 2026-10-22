package com.example.quizti.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.quizDataStore: DataStore<Preferences> by preferencesDataStore(name = "quizti_prefs")
