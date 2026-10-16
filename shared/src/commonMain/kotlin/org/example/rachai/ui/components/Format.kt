package org.example.rachai.ui.components

import kotlin.math.round

/** Formata um valor em Reais no padrão */
fun formatCurrency(amount: Double): String {
    val cents = round(amount * 100).toLong()
    val sign = if (cents < 0) "-" else ""
    val absCents = kotlin.math.abs(cents)
    val whole = absCents / 100
    val fraction = (absCents % 100).toString().padStart(2, '0')

    val wholeStr = whole.toString()
    val grouped = StringBuilder()
    for ((index, char) in wholeStr.reversed().withIndex()) {
        if (index != 0 && index % 3 == 0) grouped.append('.')
        grouped.append(char)
    }
    return "${sign}R$ ${grouped.reverse()},$fraction"
}

/** Extrai a parte "yyyy-MM-dd" de um datetime vindo do backend */
fun formatShortDate(isoDateTime: String?): String {
    if (isoDateTime.isNullOrBlank()) return ""
    val datePart = isoDateTime.substringBefore("T")
    val parts = datePart.split("-")
    if (parts.size != 3) return datePart
    val (year, month, day) = parts
    return "$day/$month/$year"
}
