package com.example.expensetracker.data.model

enum class ExpenseCategory(val displayName: String, val emoji: String) {
    Food("Food", "🍔"),
    Transport("Transport", "🚕"),
    Shopping("Shopping", "🛍️"),
    Bills("Bills", "💡"),
    Health("Health", "💊"),
    Entertainment("Entertainment", "🎬"),
    Travel("Travel", "✈️"),
    Other("Other", "📦")
}
