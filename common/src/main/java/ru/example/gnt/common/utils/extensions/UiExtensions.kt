package ru.example.gnt.common.utils.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

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


fun EditText.asFlow(resetTarget : (String) -> Unit): Flow<EditText> {
    return Channel<EditText>(capacity = Channel.UNLIMITED).also { channel -> addTextChangedListener(object :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                this@asFlow
                resetTarget(s.toString());
                channel.trySend(this@asFlow).isSuccess
            }
        }
        override fun afterTextChanged(s: Editable?) {

        }
    })
    }.receiveAsFlow()
}
