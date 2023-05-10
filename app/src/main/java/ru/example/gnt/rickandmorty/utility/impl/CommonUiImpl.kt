package ru.example.gnt.rickandmorty.utility.impl

import android.content.Context
import androidx.appcompat.app.AlertDialog
import ru.example.gnt.common.utils.AlertDialogConfig
import ru.example.gnt.common.di.deps.CommonModuleDepsProvider
import ru.example.gnt.common.utils.CommonUi
import ru.example.gnt.common.utils.extensions.showToastShort
import javax.inject.Inject

class CommonUiImpl @Inject constructor(
    private val context: Context
)  : CommonUi {

    override fun showToast(message: String?) {
        if (message != null) {
            context.showToastShort(message)
        }
    }

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        val builder = AlertDialog.Builder(context)
            .setTitle(config.title)
            .setMessage(config.message)
            .setCancelable(true)
            .setIcon(config.icon)
            .setPositiveButton(config.positiveButton) { _, _ ->
                config.positiveButtonAction?.invoke()
            }
        if (config.negativeButton != null) {
            builder.setNegativeButton(config.negativeButton) { _, _ ->
                config.negativeButtonAction?.invoke()
            }
        }
        val dialog = builder.create()
        dialog.show()
        return true
    }

}
