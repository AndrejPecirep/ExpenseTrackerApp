package com.example.expensetracker.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Analytics : NavRoutes("analytics")
    data object Settings : NavRoutes("settings")
    data object AddExpense : NavRoutes("add_expense")
    data object EditExpense : NavRoutes("edit_expense/{expenseId}") {
        fun create(expenseId: Long) = "edit_expense/$expenseId"
    }
}
