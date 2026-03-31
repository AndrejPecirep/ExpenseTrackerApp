package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.ui.components.CategoryChip
import com.example.expensetracker.ui.components.ExpenseItemCard
import com.example.expensetracker.ui.components.SummaryCard
import com.example.expensetracker.ui.model.ExpenseSortOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    expenses: List<ExpenseEntity>,
    allExpensesCount: Int,
    monthlyTotal: Double,
    total: Double,
    count: Int,
    averageExpense: Double,
    topCategoryLabel: String?,
    monthlyBudget: Double,
    searchQuery: String,
    selectedCategory: ExpenseCategory?,
    selectedSort: ExpenseSortOption,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (ExpenseCategory?) -> Unit,
    onSortChange: (ExpenseSortOption) -> Unit,
    onAddExpense: () -> Unit,
    onOpenExpense: (Long) -> Unit,
    onDeleteExpense: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Welcome back", style = MaterialTheme.typography.bodyLarge)
                    Text("Expense Tracker", style = MaterialTheme.typography.headlineMedium)
                    Text("A modern expense overview with search and analytics for daily use.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                SummaryCard(
                    monthlyTotal = monthlyTotal,
                    total = total,
                    count = count,
                    averageExpense = averageExpense,
                    monthlyBudget = monthlyBudget,
                    topCategoryLabel = topCategoryLabel
                )
            }
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search by title or note") },
                    singleLine = true
                )
            }
            item {
                Column {
                    OutlinedTextField(
                        value = selectedSort.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sort") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ExpenseSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    onSortChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                    androidx.compose.material3.Button(onClick = { expanded = true }) {
                        Text("Change sort order")
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Category filter", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategoryFilterChange(null) },
                            label = { Text("All") }
                        )
                        ExpenseCategory.entries.forEach { category ->
                            CategoryChip(category = category, selected = selectedCategory == category) { onCategoryFilterChange(category) }
                        }
                    }
                }
            }
            item {
                Text("Showing ${expenses.size} of $allExpensesCount expenses", style = MaterialTheme.typography.bodyMedium)
            }
            if (expenses.isEmpty()) {
                item {
                    Text("No results yet. Add your first expense or change the filters.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseItemCard(expense = expense, onOpen = { onOpenExpense(expense.id) }, onDelete = { onDeleteExpense(expense.id) })
                }
            }
        }
    }
}
