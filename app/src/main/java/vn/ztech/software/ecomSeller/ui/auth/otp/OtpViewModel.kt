package vn.ztech.software.ecomSeller.ui.auth.otp

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.VerifyOtpResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.ui.OTPErrors
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

private const val TAG = "OtpViewModel"

class OtpViewModel(private val useCase: IOtpUseCase) : ViewModel() {
	init {
	    Log.d("ERROR:","OtpViewModel created")
	}
	val otpStatus = MutableLiveData<VerifyOtpResponse>()
	val otpResetPasswordStatus = MutableLiveData<BasicResponse>()
	val error = MutableLiveData<CustomError>()
	val inputError = MutableLiveData<OTPErrors>()
	val loading = MutableLiveData<Boolean>()

	fun verifyOTP(phoneNumber: String, otp: String) {
		if (isOtpValid(otp))
		viewModelScope.launch {
			useCase.verifyOtp(phoneNumber, otp).flowOn(Dispatchers.IO).toLoadState().collect {
				when(it){
					LoadState.Loading -> {
						Log.d("OTP", "loading")
						loading.value = true
					}
					is LoadState.Loaded -> {
						loading.value = false
						Log.d("OTP", it.data.toString())
						otpStatus.value = it.data?: VerifyOtpResponse("")
					}
					is LoadState.Error -> {
						loading.value = false
						Log.d("OTP:ERROR:", it.e.toString())
						error.value = errorMessage(it.e)
					}
				}
			}
		}
	}

	fun verifyOTPResetPassword(phoneNumber: String, password: String, otp: String) {
		if (isOtpValid(otp))
		viewModelScope.launch {
			useCase.verifyOtpResetPassword("+84$phoneNumber", password, otp).flowOn(Dispatchers.IO).toLoadState().collect {
				when(it){
					LoadState.Loading -> {
						Log.d("OTP RESET", "loading")
						loading.value = true
					}
					is LoadState.Loaded -> {
						loading.value = false
						Log.d("OTP RESET", it.data.toString())
						otpResetPasswordStatus.value = it.data?: BasicResponse("")
					}
					is LoadState.Error -> {
						loading.value = false
						Log.d("OTP RESET:ERROR:", it.e.toString())
						error.value = errorMessage(it.e)
					}
				}
			}
		}
	}
	private fun isOtpValid(otp: String): Boolean{
		if (otp.length == 6){
			inputError.value = OTPErrors.NONE
			return true
		}else{
			inputError.value = OTPErrors.ERROR
			return false
		}
	}
	fun clearErrors() {
		error.value = null
	}
}