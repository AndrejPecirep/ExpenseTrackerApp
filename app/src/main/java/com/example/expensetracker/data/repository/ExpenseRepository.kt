package com.example.expensetracker.data.repository

import android.content.Context
import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.util.ExportManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val context: Context
) {
    fun observeExpenses(): Flow<List<ExpenseEntity>> = dao.observeExpenses()

    fun observeSummary() = observeExpenses().map { expenses ->
        val total = expenses.sumOf { it.amount }
        val monthlyExpenses = expenses.filter { ExportManager.isInCurrentMonth(it.dateEpochMillis) }
        val monthlyTotal = monthlyExpenses.sumOf { it.amount }
        val average = if (expenses.isEmpty()) 0.0 else total / expenses.size
        val largestExpense = expenses.maxByOrNull { it.amount }
        val categoryBreakdown = ExpenseCategory.entries.associateWith { category ->
            expenses.filter { it.category == category }.sumOf { it.amount }
        }
        ExpenseSummary(
            total = total,
            monthlyTotal = monthlyTotal,
            count = expenses.size,
            averageExpense = average,
            topCategory = categoryBreakdown.maxByOrNull { it.value }?.takeIf { it.value > 0 }?.key,
            largestExpense = largestExpense,
            categoryBreakdown = categoryBreakdown
        )
    }

    suspend fun addExpense(title: String, amount: Double, category: ExpenseCategory, note: String, dateEpochMillis: Long) {
        dao.insert(
            ExpenseEntity(
                title = title,
                amount = amount,
                category = category,
                note = note,
                dateEpochMillis = dateEpochMillis
            )
        )
    }

    suspend fun updateExpense(id: Long, title: String, amount: Double, category: ExpenseCategory, note: String, dateEpochMillis: Long) {
        dao.getById(id)?.let {
            dao.update(it.copy(title = title, amount = amount, category = category, note = note, dateEpochMillis = dateEpochMillis))
        }
    }

    suspend fun deleteExpense(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }

    suspend fun getExpense(id: Long): ExpenseEntity? = dao.getById(id)

    suspend fun exportCsv(expenses: List<ExpenseEntity>) = ExportManager.exportCsv(context, expenses)
    suspend fun exportPdf(expenses: List<ExpenseEntity>, summary: ExpenseSummary?) = ExportManager.exportPdf(context, expenses, summary)
}

data class ExpenseSummary(
    val total: Double,
    val monthlyTotal: Double,
    val count: Int,
    val averageExpense: Double,
    val topCategory: ExpenseCategory?,
    val largestExpense: ExpenseEntity?,
    val categoryBreakdown: Map<ExpenseCategory, Double>
)
