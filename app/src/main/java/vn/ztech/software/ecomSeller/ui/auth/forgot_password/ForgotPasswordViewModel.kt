package vn.ztech.software.ecomSeller.ui.auth.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.ui.LoginViewErrors
import vn.ztech.software.ecomSeller.ui.auth.forgot_password.IResetPasswordUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import vn.ztech.software.ecomSeller.util.isPasswordValid
import vn.ztech.software.ecomSeller.util.isPhoneValid

class ForgotPasswordViewModel(private val useCase: IResetPasswordUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError?>()
    val errorInputData = MutableLiveData<LoginViewErrors>()
    val isSentResetRequestSuccessfully = MutableLiveData<Boolean>()
    var phoneNumber: String? = null
    //todo: cache user data
    fun resetPassword(phoneNumber: String, password: String, retypePassword: String) {
        if (isInfoValid(phoneNumber, password, retypePassword))
            viewModelScope.launch {
                useCase.sendResetPasswordRequest("+84$phoneNumber", password).flowOn(Dispatchers.IO).toLoadState().collect {
                    when (it) {
                        is LoadState.Loading -> {
                            loading.value = true
                        }
                        is LoadState.Loaded -> {
                            Log.d("RESET:", "LoadState.Loaded ${it.data}")
                            loading.value = false
                            isSentResetRequestSuccessfully.value = true
                        }
                        is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }xxx
                            Log.d("RESET:", "LoadState.Error ${it.e.message}")
                            loading.value = false
                            error.value = errorMessage(it.e)
                        }
                    }
                }
            }
    }

    private fun isInfoValid(phoneNumber: String, password: String, retypePassword: String): Boolean {
        if (phoneNumber.isBlank() || password.isBlank() || retypePassword.isBlank()) {
            errorInputData.value = LoginViewErrors.ERR_EMPTY
            return false
        }
        if (!isPhoneValid(phoneNumber)) {
            errorInputData.value = LoginViewErrors.ERR_MOBILE
            return false
        }
        if (password != retypePassword){
            errorInputData.value = LoginViewErrors.ERR_RETYPE_PASSWORD
            return false
        }
        if (!isPasswordValid(password)) {
            errorInputData.value = LoginViewErrors.ERR_PASSWORD
            return false
        }
        errorInputData.value = LoginViewErrors.NONE
        return true
    }
    fun clearErrors() {
        error.value = null
    }
}