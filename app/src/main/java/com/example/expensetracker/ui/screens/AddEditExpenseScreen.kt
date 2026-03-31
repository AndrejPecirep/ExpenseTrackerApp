package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.ui.components.CategoryChip

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseScreen(expense: ExpenseEntity?, onSave: (Long?, String, Double, ExpenseCategory, String, Long) -> Unit) {
    var title by remember(expense) { mutableStateOf(expense?.title.orEmpty()) }
    var amount by remember(expense) { mutableStateOf(if (expense == null) "" else expense.amount.toString()) }
    var note by remember(expense) { mutableStateOf(expense?.note.orEmpty()) }
    var selectedCategory by remember(expense) { mutableStateOf(expense?.category ?: ExpenseCategory.Food) }
    var dateEpochMillis by remember(expense) { mutableLongStateOf(expense?.dateEpochMillis ?: System.currentTimeMillis()) }

    Scaffold(topBar = { TopAppBar(title = { Text(if (expense == null) "New expense" else "Edit expense") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }.replace(',', '.') },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text("Kategorija", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpenseCategory.entries.forEach { category ->
                    CategoryChip(category = category, selected = selectedCategory == category) { selectedCategory = category }
                }
            }
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Text("The date is currently set to today for simple offline use without additional permissions.")
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amountValue > 0) {
                        onSave(expense?.id, title.trim(), amountValue, selectedCategory, note.trim(), dateEpochMillis)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
