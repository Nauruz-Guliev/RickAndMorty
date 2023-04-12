package ru.example.gnt.common

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

fun ImageView.setImageDrawable(@DrawableRes id: Int) {
    AppCompatResources.getDrawable(
        this.context,
        id
    )
}
