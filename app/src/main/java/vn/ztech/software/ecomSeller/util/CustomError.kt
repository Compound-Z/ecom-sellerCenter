package vn.ztech.software.ecomSeller.util

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import vn.ztech.software.ecomSeller.R
import java.io.IOException
import java.io.Serializable

open class CustomError( val e: Throwable = Throwable(), open var customMessage: String = "System error! Please try again later!"): IOException(), Serializable{
    fun showErrorDialog(context: Context, listener: DialogInterface.OnClickListener? = null): AlertDialog {
        Log.d("showErrorDialog", customMessage)
        return AlertDialog.Builder(context)
            .setTitle(R.string.error_dialog_tittle)
            .setMessage(customMessage)
            .setPositiveButton(R.string.ok, listener)
            .show()
    }
}