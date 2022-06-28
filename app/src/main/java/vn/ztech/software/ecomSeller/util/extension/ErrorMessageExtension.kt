package vn.ztech.software.ecomSeller.util.extension

import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import androidx.fragment.app.Fragment
import vn.ztech.software.ecomSeller.util.CustomError


fun Activity.showErrorDialog(e: CustomError, listener: DialogInterface.OnClickListener? = null) {
    Log.d("ERROR:", "Activity.showErrorDialog")
//    if (refreshTokenExpiredError(e)) {
//        return
//    }
//    if (accountErrorError(e)) {
//        return
//    }
//    if (accountPerrmissionError(e)) {
//        return
//    }
    e.showErrorDialog(this, listener)
}

fun Fragment.showErrorDialog(e: CustomError, listener: DialogInterface.OnClickListener? = null) {
    requireActivity().showErrorDialog(e, listener)
}