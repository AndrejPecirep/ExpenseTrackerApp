package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.data.preferences.UserPreferencesRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.screens.AddEditExpenseScreen
import com.example.expensetracker.ui.screens.AnalyticsScreen
import com.example.expensetracker.ui.screens.HomeScreen
import com.example.expensetracker.ui.screens.SettingsScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.ui.viewmodel.ExpenseViewModelFactory

@Composable
fun ExpenseTrackerApp(
    repository: ExpenseRepository,
    preferencesRepository: UserPreferencesRepository
) {
    val navController = rememberNavController()
    val viewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(repository, preferencesRepository))
    val expenses by viewModel.expenses.collectAsStateCompat()
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateCompat()
    val summary by viewModel.summary.collectAsStateCompat()
    val selectedExpense by viewModel.selectedExpense.collectAsStateCompat()
    val exportedFile by viewModel.exportedFile.collectAsStateCompat()
    val filters by viewModel.filters.collectAsStateCompat()
    val preferences by viewModel.preferences.collectAsStateCompat()

    val bottomDestinations = listOf(NavRoutes.Home, NavRoutes.Analytics, NavRoutes.Settings)

    ExpenseTrackerTheme(darkTheme = preferences.darkMode) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                if (currentDestination?.route in bottomDestinations.map { it.route }) {
                    NavigationBar {
                        bottomDestinations.forEach { destination ->
                            val icon = when (destination) {
                                NavRoutes.Home -> Icons.Outlined.Home
                                NavRoutes.Analytics -> Icons.Outlined.Analytics
                                else -> Icons.Outlined.Settings
                            }
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    }
                                },
                                icon = { Icon(icon, contentDescription = destination.route) },
                                label = { Text(destination.route.substringBefore("/").replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            }
        ) { _ ->
            NavHost(navController = navController, startDestination = NavRoutes.Home.route, modifier = Modifier) {
                composable(NavRoutes.Home.route) {
                    HomeScreen(
                        expenses = filteredExpenses,
                        allExpensesCount = expenses.size,
                        monthlyTotal = summary?.monthlyTotal ?: 0.0,
                        total = summary?.total ?: 0.0,
                        count = summary?.count ?: 0,
                        averageExpense = summary?.averageExpense ?: 0.0,
                        topCategoryLabel = summary?.topCategory?.displayName,
                        monthlyBudget = preferences.monthlyBudget,
                        searchQuery = filters.query,
                        selectedCategory = filters.category,
                        selectedSort = filters.sortOption,
                        onSearchChange = viewModel::updateSearchQuery,
                        onCategoryFilterChange = viewModel::updateCategoryFilter,
                        onSortChange = viewModel::updateSortOption,
                        onAddExpense = { navController.navigate(NavRoutes.AddExpense.route) },
                        onOpenExpense = { id -> viewModel.loadExpense(id); navController.navigate(NavRoutes.EditExpense.create(id)) },
                        onDeleteExpense = viewModel::deleteExpense
                    )
                }
                composable(NavRoutes.Analytics.route) {
                    AnalyticsScreen(
                        categoryTotals = summary?.categoryBreakdown ?: emptyMap(),
                        total = summary?.total ?: 0.0,
                        monthlyTotal = summary?.monthlyTotal ?: 0.0,
                        topCategory = summary?.topCategory?.displayName,
                        largestExpenseTitle = summary?.largestExpense?.title,
                        largestExpenseAmount = summary?.largestExpense?.amount ?: 0.0,
                        budget = preferences.monthlyBudget
                    )
                }
                composable(NavRoutes.Settings.route) {
                    SettingsScreen(
                        exportedFile = exportedFile,
                        darkMode = preferences.darkMode,
                        budget = preferences.monthlyBudget,
                        onDarkModeChange = viewModel::setDarkMode,
                        onBudgetSave = viewModel::setMonthlyBudget,
                        onExportCsvClick = viewModel::exportCsv,
                        onExportPdfClick = viewModel::exportPdf
                    )
                }
                composable(NavRoutes.AddExpense.route) {
                    AddEditExpenseScreen(expense = null) { id, title, amount, category, note, date ->
                        viewModel.saveExpense(id, title, amount, category, note, date) { navController.popBackStack() }
                    }
                }
                composable(
                    route = NavRoutes.EditExpense.route,
                    arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
                    if (selectedExpense?.id != expenseId) viewModel.loadExpense(expenseId)
                    AddEditExpenseScreen(expense = selectedExpense) { id, title, amount, category, note, date ->
                        viewModel.saveExpense(id, title, amount, category, note, date) { navController.popBackStack() }
                    }
                }
            }
        }
    }
}
