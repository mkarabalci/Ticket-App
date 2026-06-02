package com.example.core.util


// Tüm projedeki "Tarihsel" formatlayıcı..

private val turkishMonthsShort = arrayOf(
    "Oca","Şub","Mar","Nis","May","Haz","Tem","Ağu","Eyl","Eki","Kas","Ara"
)

fun formatDate(iso: String): String {
    return try {
        val datePart = iso.substringBefore("T")
        val parts = datePart.split("-")
        val year = parts[0]
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val monthName = turkishMonthsShort[month - 1]

        val timePart = iso.substringAfter("T", "").take(5)

        "$day $monthName $year $timePart"
    } catch (e: Exception) {
        iso
    }
}
