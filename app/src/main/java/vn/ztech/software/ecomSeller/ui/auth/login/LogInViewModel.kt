package vn.ztech.software.ecomSeller.ui.auth.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.ui.LoginViewErrors
import vn.ztech.software.ecomSeller.ui.UserType
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import vn.ztech.software.ecomSeller.util.isPasswordValid
import vn.ztech.software.ecomSeller.util.isPhoneValid

class LogInViewModel(private val useCase: ILogInUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError?>()
    val errorInputData = MutableLiveData<LoginViewErrors>()
    val isLogInSuccessfully = MutableLiveData<Boolean>()
    var tokens: TokenResponse? = null
    var userData: UserData? = null

    //todo: cache user data
    fun login(phoneNumber: String, password: String) {
        if (isLogInInfoValid(phoneNumber, password))
            viewModelScope.launch {
                useCase.login("+84$phoneNumber", password).flowOn(Dispatchers.IO).toLoadState().collect {
                    when (it) {
                        is LoadState.Loading -> {
                            loading.value = true
                        }
                        is LoadState.Loaded -> {
                            Log.d("LOGIN:", "LoadState.Loaded ${it.data}")
                            if(checkIdSeller(it.data.user)){
                                userData = it.data.user
                                tokens = it.data.tokens
                                loading.value = false
                                saveLogInInfo(userData, tokens)
                                isLogInSuccessfully.value = true
                            }else{
                                loading.value = false
                                error.value = errorMessage(CustomError(customMessage = "Wrong account type, please use an Seller account to use this app"))
                            }

                        }
                        is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }xxx
                            Log.d("LOGIN:", "LoadState.Error ${it.e.message}")
                            loading.value = false
                            error.value = errorMessage(it.e)
                        }
                    }
                }
            }
    }

    private fun checkIdSeller(user: UserData): Boolean {
        return user.role == UserType.seller.name
    }

    private fun saveLogInInfo(userData: UserData?, tokens: TokenResponse?) {
        if(userData != null && tokens != null){
            Log.d("LOGIN", "LogInViewModel saveLogInInfo")
            useCase.saveLogInInfo(userData, tokens)
        }
    }

    private fun isLogInInfoValid(phoneNumber: String, password: String): Boolean {
        if (phoneNumber.isBlank() || password.isBlank()) {
            errorInputData.value = LoginViewErrors.ERR_EMPTY
            return false
        }
        if (!isPhoneValid(phoneNumber)) {
            errorInputData.value = LoginViewErrors.ERR_MOBILE
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