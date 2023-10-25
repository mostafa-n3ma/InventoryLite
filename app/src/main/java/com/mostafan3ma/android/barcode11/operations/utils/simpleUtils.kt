package com.mostafan3ma.android.barcode11.operations.utils

import android.content.Context
import android.util.TypedValue
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

// value in DP
fun getValueInDP(context: Context, value: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun getValueInDP(context: Context, value: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        context.resources.displayMetrics
    )
}