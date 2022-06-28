package vn.ztech.software.ecomSeller.ui.auth.otp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.VerifyOtpResponse
import vn.ztech.software.ecomSeller.repository.IAuthRepository

interface IOtpUseCase{
    fun verifyOtp(phoneNumber: String, otpCode: String): Flow<VerifyOtpResponse>
    fun verifyOtpResetPassword(phoneNumber: String, password: String, otp: String): Flow<BasicResponse>
}

class OtpUseCase(private val authRepos: IAuthRepository): IOtpUseCase{
    override fun verifyOtp(phoneNumber: String, otpCode: String): Flow<VerifyOtpResponse> = flow {
        emit(authRepos.verifyOtp(phoneNumber, otpCode))
    }

    override fun verifyOtpResetPassword(phoneNumber: String, password: String, otp: String) = flow {
        emit(authRepos.verifyOtpResetPassword(phoneNumber, password, otp))
    }

}