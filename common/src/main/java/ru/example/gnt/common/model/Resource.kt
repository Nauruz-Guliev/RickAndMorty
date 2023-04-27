package ru.example.gnt.common.model

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources


sealed class Resource(
    @AnyRes val id: Int
) {

    /**
     * @param id resource id
     * @param argument a single optional item. Since you can not create a class instance with vararg,
     * this workaround is used. For multiple arguments use resources.getString(id, vararg)
     */
    class String(
        @StringRes id: Int,
        val argument: Any? = null,
    ) : Resource(id) {
        fun getValue(context: Context) = context.resources.getString(id, argument.toString())
    }

    class Drawable(@DrawableRes id: Int) : Resource(id) {
        fun getValue(context: Context) =
            AppCompatResources.getDrawable(context, id)
    }

    class Color(@ColorRes id: Int) : Resource(id) {
        fun getValue(context: Context): ColorStateList =
            AppCompatResources.getColorStateList(context, id)
    }
    class Dimension(@DimenRes id: Int) : Resource(id) {
        fun getValue(context: Context) = context.resources.getDimension(id)
    }
}
