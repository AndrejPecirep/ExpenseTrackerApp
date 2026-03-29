package com.example.expensetracker.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val darkMode = booleanPreferencesKey("dark_mode")
        val monthlyBudget = doublePreferencesKey("monthly_budget")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs: Preferences ->
        UserPreferences(
            darkMode = prefs[Keys.darkMode] ?: false,
            monthlyBudget = prefs[Keys.monthlyBudget] ?: 0.0
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.darkMode] = enabled }
    }

    suspend fun setMonthlyBudget(value: Double) {
        context.dataStore.edit { it[Keys.monthlyBudget] = value }
    }
}

data class UserPreferences(
    val darkMode: Boolean = false,
    val monthlyBudget: Double = 0.0
)
