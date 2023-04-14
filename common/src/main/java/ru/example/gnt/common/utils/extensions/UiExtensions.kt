package ru.example.gnt.common.utils.extensions

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar

fun ImageView.setImageDrawable(@DrawableRes id: Int) {
    this.setImageDrawable(
        AppCompatResources.getDrawable(
            context,
            id
        )
    )
}

fun <T> Context?.showToastShort(message: T?) {
    Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
}

fun <T> Context?.showToastLong(message: T?) {
    Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show()
}

fun <T> View.showSimpleSnackbar(
    message: T?,
    action: (() -> Unit)? = null,
    actionMessage: String? = null
) {
    Snackbar.make(
        this,
        message.toString(), Snackbar.LENGTH_SHORT
    ).apply {
        if (action != null && actionMessage != null) {
            setAction(actionMessage) { action.invoke() }
        }
    }.also { snackbar ->
        snackbar.show()
    }
}