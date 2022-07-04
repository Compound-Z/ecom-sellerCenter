package vn.ztech.software.ecomSeller.util

import android.util.Log
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.exception.ResourceException
import vn.ztech.software.ecomSeller.exception.UnauthenticatedException
import java.net.ConnectException
import java.net.SocketTimeoutException

fun errorMessage(
    e: CustomError
): CustomError {
    Log.d("errorMessage", e.e.toString())
    when (e) {
        is UnauthenticatedException -> return e
        is RefreshTokenExpiredException -> return e
        is ResourceException -> return e
    }
    when (e.e){
        is ConnectException -> {
            e.customMessage = "Connection failed! Please check your internet!"
        }
        is SocketTimeoutException -> {
            e.customMessage = "Timeout! Please try again later!"
        }
        is CustomError->{
            e.customMessage = e.e.customMessage
        }
    }
    return e

}