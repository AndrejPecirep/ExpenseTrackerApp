package com.example.expensetracker.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.ExpenseCategory

@Composable
fun CategoryChip(category: ExpenseCategory, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("${category.emoji} ${category.displayName}") }
    )
}
