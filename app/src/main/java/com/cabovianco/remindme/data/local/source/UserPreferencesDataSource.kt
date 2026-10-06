package com.cabovianco.remindme.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val SHOW_WELCOME_SCREEN = booleanPreferencesKey("showWelcomeScreen")
        val IS_HISTORY_ENABLED = booleanPreferencesKey("isHistoryEnabled")
        val HISTORY_RETENTION_DAYS = intPreferencesKey("historyRetentionDays")
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

    val isHistoryEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_HISTORY_ENABLED] ?: true
        }

    suspend fun setIsHistoryEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_HISTORY_ENABLED] = enabled
        }
    }

    val historyRetentionDays: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[HISTORY_RETENTION_DAYS] ?: 3
        }

    suspend fun setHistoryRetentionDays(days: Int) {
        dataStore.edit { preferences ->
            preferences[HISTORY_RETENTION_DAYS] = days
        }
    }
}
