package ru.example.gnt.common

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

fun ImageView.setImageDrawable(@DrawableRes id: Int) {
    this.setImageDrawable(
        AppCompatResources.getDrawable(
            context,
            id
        )
    )
}
