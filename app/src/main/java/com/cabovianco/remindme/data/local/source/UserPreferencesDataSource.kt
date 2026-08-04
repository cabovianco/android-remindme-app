package com.cabovianco.remindme.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val SHOW_WELCOME_SCREEN = booleanPreferencesKey("showWelcomeScreen")
    }

    val showWelcomeScreen: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SHOW_WELCOME_SCREEN] ?: true
        }

    suspend fun setShowWelcomeScreen(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_WELCOME_SCREEN] = show
        }
    }
}
