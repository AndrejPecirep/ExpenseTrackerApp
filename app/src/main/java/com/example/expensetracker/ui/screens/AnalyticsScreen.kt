package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.util.toCurrency

@Composable
fun AnalyticsScreen(
    categoryTotals: Map<ExpenseCategory, Double>,
    total: Double,
    monthlyTotal: Double,
    topCategory: String?,
    largestExpenseTitle: String?,
    largestExpenseAmount: Double,
    budget: Double
) {
    val grandTotal = categoryTotals.values.sum().takeIf { it > 0 } ?: 1.0
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Analitika", style = MaterialTheme.typography.headlineMedium)
                Text("A clear overview of spending by category and key KPI metrics.")
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricBlock("Ukupno", total.toCurrency())
                        MetricBlock("Mjesec", monthlyTotal.toCurrency())
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricBlock("Top category", topCategory ?: "—")
                        MetricBlock("Largest expense", if (largestExpenseTitle != null) "${largestExpenseAmount.toCurrency()}" else "—")
                    }
                    if (budget > 0) {
                        Text("Budget: ${budget.toCurrency()}")
                    }
                }
            }
        }
        items(categoryTotals.entries.filter { it.value > 0.0 }.sortedByDescending { it.value }) { entry ->
            val progress = (entry.value / grandTotal).toFloat()
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${entry.key.emoji} ${entry.key.displayName}", style = MaterialTheme.typography.titleMedium)
                    Text(entry.value.toCurrency(), style = MaterialTheme.typography.bodyLarge)
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun MetricBlock(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
