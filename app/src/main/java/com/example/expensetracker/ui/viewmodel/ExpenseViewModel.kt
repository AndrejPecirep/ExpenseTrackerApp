package com.example.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.preferences.UserPreferences
import com.example.expensetracker.data.preferences.UserPreferencesRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.ExpenseSummary
import com.example.expensetracker.ui.model.ExpenseFilters
import com.example.expensetracker.ui.model.ExpenseSortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(ExpenseFilters())
    val filters: StateFlow<ExpenseFilters> = _filters

    val expenses = repository.observeExpenses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val summary: StateFlow<ExpenseSummary?> = repository.observeSummary().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserPreferences()
    )

    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(expenses, filters) { expensesList, currentFilters ->
        expensesList
            .filter { expense ->
                (currentFilters.query.isBlank() || expense.title.contains(currentFilters.query.trim(), ignoreCase = true) || expense.note.contains(currentFilters.query.trim(), ignoreCase = true)) &&
                    (currentFilters.category == null || expense.category == currentFilters.category)
            }
            .let { filtered ->
                when (currentFilters.sortOption) {
                    ExpenseSortOption.Newest -> filtered.sortedByDescending { it.dateEpochMillis }
                    ExpenseSortOption.Oldest -> filtered.sortedBy { it.dateEpochMillis }
                    ExpenseSortOption.HighestAmount -> filtered.sortedByDescending { it.amount }
                    ExpenseSortOption.LowestAmount -> filtered.sortedBy { it.amount }
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedExpense = MutableStateFlow<ExpenseEntity?>(null)
    val selectedExpense: StateFlow<ExpenseEntity?> = _selectedExpense

    private val _exportedFile = MutableStateFlow<File?>(null)
    val exportedFile: StateFlow<File?> = _exportedFile

    fun loadExpense(id: Long) {
        viewModelScope.launch { _selectedExpense.value = repository.getExpense(id) }
    }

    fun updateSearchQuery(value: String) {
        _filters.value = _filters.value.copy(query = value)
    }

    fun updateCategoryFilter(category: ExpenseCategory?) {
        _filters.value = _filters.value.copy(category = category)
    }

    fun updateSortOption(option: ExpenseSortOption) {
        _filters.value = _filters.value.copy(sortOption = option)
    }

    fun saveExpense(id: Long?, title: String, amount: Double, category: ExpenseCategory, note: String, dateEpochMillis: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            if (id == null) repository.addExpense(title, amount, category, note, dateEpochMillis)
            else repository.updateExpense(id, title, amount, category, note, dateEpochMillis)
            onDone()
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch { repository.deleteExpense(id) }
    }

    fun exportCsv() {
        viewModelScope.launch { _exportedFile.value = repository.exportCsv(expenses.value) }
    }

    fun exportPdf() {
        viewModelScope.launch { _exportedFile.value = repository.exportPdf(expenses.value, summary.value) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setDarkMode(enabled) }
    }

    fun setMonthlyBudget(value: Double) {
        viewModelScope.launch { preferencesRepository.setMonthlyBudget(value) }
    }
}

class ExpenseViewModelFactory(
    private val repository: ExpenseRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
