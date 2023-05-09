package ru.example.gnt.common.utils.extensions

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

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
    actionMessage: String? = null,
    infinite: Boolean = false
) {
    Snackbar.make(
        this,
        message.toString(), if (infinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT
    ).apply {
        if (action != null && actionMessage != null) {
            setAction(actionMessage) { action.invoke() }
        }
    }.also { snackbar ->
        snackbar.anchorView = this
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
                resetTarget(s.toString());
                channel.trySend(this@asFlow).isSuccess
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    })
    }.receiveAsFlow()
}

fun <T : Enum<*>> ChipGroup.createChip(enum: T): Chip {
    return Chip(this.context).apply {
        when (enum) {
            is CharacterStatusEnum -> {
                text = enum.value
                setChipBackgroundColorResource(enum.color.id)
            }
            is CharacterGenderEnum -> {
                text = enum.value
                setChipBackgroundColorResource(ru.example.gnt.ui.R.color.blue_rm_secondary)
                setTextColor(
                    AppCompatResources.getColorStateList(
                        context,
                        ru.example.gnt.ui.R.color.blue_rm
                    ).defaultColor
                )
            }
        }
        isCloseIconVisible = false
        isCheckable = true
    }
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
