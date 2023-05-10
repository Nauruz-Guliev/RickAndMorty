package ru.example.gnt.common.utils

interface CommonUi {
    fun showToast(message: String?)
    suspend fun showAlertDialog(config: AlertDialogConfig):Boolean
}
