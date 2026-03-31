package com.example.expensetracker.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun SettingsScreen(
    exportedFile: File?,
    darkMode: Boolean,
    budget: Double,
    onDarkModeChange: (Boolean) -> Unit,
    onBudgetSave: (Double) -> Unit,
    onExportCsvClick: () -> Unit,
    onExportPdfClick: () -> Unit
) {
    val context = LocalContext.current
    var budgetInput by remember(budget) { mutableStateOf(if (budget == 0.0) "" else budget.toString()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("App appearance", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Dark theme")
                    Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
                }
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { budgetInput = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }.replace(',', '.') },
                    label = { Text("Monthly budget") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(onClick = { onBudgetSave(budgetInput.toDoubleOrNull() ?: 0.0) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Save budget")
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Export and sharing", style = MaterialTheme.typography.titleMedium)
                Text("The app works fully offline and does not require a paid API or mandatory backend.")
                Button(onClick = onExportPdfClick, modifier = Modifier.fillMaxWidth()) { Text("Export PDF report") }
                Button(onClick = onExportCsvClick, modifier = Modifier.fillMaxWidth()) { Text("Export CSV report") }
                if (exportedFile != null) {
                    Text("Saved: ${exportedFile.name}")
                    Button(
                        onClick = {
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", exportedFile)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = if (exportedFile.extension.lowercase() == "pdf") "application/pdf" else "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share report"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Share last export") }
                }
            }
        }
    }
}
