package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val note: String = "",
    val dateEpochMillis: Long,
    val createdAt: Long = System.currentTimeMillis()
)
