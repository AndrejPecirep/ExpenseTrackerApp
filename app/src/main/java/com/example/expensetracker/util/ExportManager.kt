package com.example.expensetracker.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseSummary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ExportManager {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun isInCurrentMonth(epochMillis: Long): Boolean {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = epochMillis }
        return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) && now.get(Calendar.MONTH) == date.get(Calendar.MONTH)
    }

    suspend fun exportCsv(context: Context, expenses: List<ExpenseEntity>): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "expense_report_${System.currentTimeMillis()}.csv")
        file.writeText(buildString {
            appendLine("Title,Amount,Category,Date,Note")
            expenses.forEach { expense ->
                appendLine("\"${expense.title.escapeCsv()}\",${expense.amount},${expense.category.displayName},${dateFormat.format(Date(expense.dateEpochMillis))},\"${expense.note.escapeCsv()}\"")
            }
        })
        return file
    }

    suspend fun exportPdf(context: Context, expenses: List<ExpenseEntity>, summary: ExpenseSummary?): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "expense_report_${System.currentTimeMillis()}.pdf")
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 22f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 12f }
        val boldPaint = Paint().apply { textSize = 12f; isFakeBoldText = true }

        var y = 48f
        canvas.drawText("Expense Tracker Report", 40f, y, titlePaint)
        y += 24f
        canvas.drawText("Generated: ${dateFormat.format(Date())}", 40f, y, bodyPaint)
        y += 28f

        summary?.let {
            canvas.drawText("Total: ${it.total.toCurrency()}", 40f, y, boldPaint)
            y += 18f
            canvas.drawText("This month: ${it.monthlyTotal.toCurrency()}", 40f, y, bodyPaint)
            y += 18f
            canvas.drawText("Number of expenses: ${it.count}", 40f, y, bodyPaint)
            y += 18f
            canvas.drawText("Average: ${it.averageExpense.toCurrency()}", 40f, y, bodyPaint)
            y += 26f
        }

        canvas.drawText("Last ${expenses.take(20).size} expenses", 40f, y, boldPaint)
        y += 20f
        expenses.take(20).forEachIndexed { index, expense ->
            val line = "${index + 1}. ${expense.title} • ${expense.category.displayName} • ${expense.amount.toCurrency()} • ${dateFormat.format(Date(expense.dateEpochMillis))}"
            canvas.drawText(line.take(85), 40f, y, bodyPaint)
            y += 18f
            if (y > 800f) return@forEachIndexed
        }

        document.finishPage(page)
        file.outputStream().use { document.writeTo(it) }
        document.close()
        return file
    }

    private fun String.escapeCsv() = replace("\"", "\"\"")
}
