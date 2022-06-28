package vn.ztech.software.ecomSeller.ui.auth.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.ui.SignUpViewErrors
import vn.ztech.software.ecomSeller.ui.UserType
import vn.ztech.software.ecomSeller.util.*
import vn.ztech.software.ecomSeller.util.isEmailValid
import vn.ztech.software.ecomSeller.util.isPhoneValid

class SignUpViewModel(private val useCase: ISignUpUseCase): ViewModel() {
    init {
        Log.d("ERROR:","SignUpViewModel created")

    }
    private val _errorStatus = MutableLiveData<SignUpViewErrors?>()
    val errorStatus: LiveData<SignUpViewErrors?> get() = _errorStatus

    val signUpError = MutableLiveData<CustomError?>()

    val isSignUpSuccessfully = MutableLiveData<Boolean?>()

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData

    val loading = MutableLiveData<Boolean>()

    fun signUpSubmitData(
        name: String,
        mobile: String,
        email: String,
        pwd1: String,
        pwd2: String,
        isAccepted: Boolean,
        isSeller: Boolean = false
    ) {
        if (name.isBlank() || mobile.isBlank() || email.isBlank() || pwd1.isBlank() || pwd2.isBlank()) {
            _errorStatus.value = SignUpViewErrors.ERR_EMPTY
        } else {
            if (pwd1 != pwd2) {
                _errorStatus.value = SignUpViewErrors.ERR_PWD12NS
            } else {
                if(!isPasswordValid(pwd1) || !isPasswordValid(pwd2)){
                    _errorStatus.value = SignUpViewErrors.ERR_PW_INVALID
                }else{
                    if (!isAccepted) {
                        _errorStatus.value = SignUpViewErrors.ERR_NOT_ACC
                    } else {
                        var err = ERR_INIT
                        if (!isEmailValid(email)) {
                            err += ERR_EMAIL
                        }
                        if (!isPhoneValid(mobile)) {
                            err += ERR_MOBILE
                        }
                        when (err) {
                            ERR_INIT -> {
                                _errorStatus.value = SignUpViewErrors.NONE
                                val uId = getRandomString(32, "84" + mobile.trim(), 6)
                                val newData =
                                    UserData(
                                        uId,
                                        name.trim(),
                                        "+84" + mobile.trim(),
                                        email.trim(),
                                        pwd1.trim(),
                                        UserType.CUSTOMER.name,
                                        ArrayList(),
                                        ArrayList(),
//                                        ArrayList(),
                                        /**if (isSeller) UserType.SELLER.name else */
                                        /**if (isSeller) UserType.SELLER.name else */
                                    )
                                _userData.value = newData
                                sendSignUpRequest(newData)
                            }
                            (ERR_INIT + ERR_EMAIL) -> _errorStatus.value = SignUpViewErrors.ERR_EMAIL
                            (ERR_INIT + ERR_MOBILE) -> _errorStatus.value = SignUpViewErrors.ERR_MOBILE
                            (ERR_INIT + ERR_EMAIL + ERR_MOBILE) -> _errorStatus.value = SignUpViewErrors.ERR_EMAIL_MOBILE
                        }
                    }
                }
            }
        }

    }

    private fun sendSignUpRequest(user: UserData) {
        viewModelScope.launch {
            useCase.sendSignUpRequest(user).flowOn(Dispatchers.IO).toLoadState().collect{
                when (it) {
                    is LoadState.Loading -> {
                        loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        Log.d("ERROR:", "LoadState.Loaded $it, id: ${this@launch}")
                        isSignUpSuccessfully.value = true

                    }
                    is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }xxx
                        Log.d("ERROR:", "LoadState.Error ${it.e.message}")
                        loading.value = false
                        signUpError.value = errorMessage(it.e)

                    }
                }
            }
        }
    }
    fun clearError(){
        _errorStatus.value = null
        isSignUpSuccessfully.value = null
        signUpError.value = null
    }
}