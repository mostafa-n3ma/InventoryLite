package com.mostafan3ma.android.barcode11.oporations.utils

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Date()
    return dateFormat.format(currentDate)
}



fun generateUniqueId(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd/HH:mm.ss", Locale.getDefault())
    val datePart = dateFormat.format(Date())

    val randomLetter = ('A'..'Z').random()

    return "$datePart $randomLetter"
}