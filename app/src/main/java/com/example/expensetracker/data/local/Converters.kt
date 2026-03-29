package com.example.expensetracker.data.local

import androidx.room.TypeConverter
import com.example.expensetracker.data.model.ExpenseCategory

class Converters {
    @TypeConverter
    fun fromCategory(category: ExpenseCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)
}
