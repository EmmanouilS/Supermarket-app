package com.example.supermarketapp.util

import android.content.Context

fun Context.drawableIdOrNull(name: String?): Int? =
    name?.let { resources.getIdentifier(it, "drawable", packageName) }?.takeIf { it != 0 }

