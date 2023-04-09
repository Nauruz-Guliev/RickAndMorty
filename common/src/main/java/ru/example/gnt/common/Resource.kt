package ru.example.gnt.common

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources


sealed class Resource(
    @AnyRes val id: Int
) {
    class String(@StringRes id: Int) : Resource(id) {
        fun getValue(context: Context) = context.resources.getString(id)
    }

    class Drawable(@DrawableRes id: Int) : Resource(id) {
        fun getValue(context: Context) =
            AppCompatResources.getDrawable(context, id)
    }

    class Color(@ColorRes id: Int) : Resource(id) {
        fun getValue(context: Context): ColorStateList = AppCompatResources.getColorStateList(context, id)
    }
    class Dimension(@DimenRes id: Int) : Resource(id) {
        fun getValue(context: Context) = context.resources.getDimension(id)
    }
}
