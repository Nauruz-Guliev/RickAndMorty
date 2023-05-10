package ru.example.gnt.common.utils

import android.graphics.drawable.Drawable

data class AlertDialogConfig(
    val title: String,
    val message: String,
    val positiveButton: String,
    val negativeButton: String? = null,
    val positiveButtonAction: (() -> Unit)? = null,
    val negativeButtonAction: (() -> Unit)? = null,
    val icon: Drawable
)
