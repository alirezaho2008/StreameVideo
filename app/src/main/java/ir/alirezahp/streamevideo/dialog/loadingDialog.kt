package ir.alirezahp.streamevideo.dialog

import android.app.AlertDialog
import android.content.Context
import ir.alirezahp.streamevideo.R

private lateinit var loadingDialog: LoadingDialog



class LoadingDialog(context: Context) {
    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.dialog_loading)
        builder.setCancelable(false)

        dialog = builder.create()
//        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable()) // پس‌زمینه شفاف
    }
    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }}