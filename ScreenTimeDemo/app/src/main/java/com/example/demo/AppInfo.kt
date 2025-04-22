package com.example.demo

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    val category: String, // "ENTERTAINMENT", "SOCIAL", "OTHER"
    var isBlocked: Boolean = false
)