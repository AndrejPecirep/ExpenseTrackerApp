package com.example.expensetracker.ui.model

import com.example.expensetracker.data.model.ExpenseCategory

enum class ExpenseSortOption(val label: String) {
    Newest("Newest"),
    Oldest("Oldest"),
    HighestAmount("Highest amount"),
    LowestAmount("Lowest amount")
}

data class ExpenseFilters(
    val query: String = "",
    val category: ExpenseCategory? = null,
    val sortOption: ExpenseSortOption = ExpenseSortOption.Newest
)
