package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.expensetracker.data.local.AppDatabase
import com.example.expensetracker.data.preferences.UserPreferencesRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.navigation.ExpenseTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.expenseDao(), applicationContext)
        val preferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            ExpenseTrackerApp(
                repository = repository,
                preferencesRepository = preferencesRepository
            )
        }
    }
}
