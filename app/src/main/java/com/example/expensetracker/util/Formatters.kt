package com.example.expensetracker.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
private val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

fun Double.toCurrency(): String = currencyFormatter.format(this)
fun Long.toReadableDate(): String = dateFormatter.format(Date(this))
fun currentMonthLabel(): String = monthFormatter.format(Date()).replaceFirstChar { it.titlecase(Locale.ENGLISH) }
