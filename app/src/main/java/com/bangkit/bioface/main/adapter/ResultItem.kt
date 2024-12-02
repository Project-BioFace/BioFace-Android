package com.bangkit.bioface.main.adapter

import android.graphics.Bitmap

data class ResultItem(
    val image: Bitmap,
    val condition: String,
    val recommendation: String,
    val timestamp: Long // Menyimpan waktu pemrosesan
)
