package vn.ztech.software.ecomSeller.repository

import android.util.Log
import org.koin.androidx.viewmodel.ext.android.getViewModelFactory
import vn.ztech.software.ecomSeller.api.IAuthApi
import vn.ztech.software.ecomSeller.api.request.*
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.LogInResponse
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.api.response.VerifyOtpResponse
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.model.UserData
import java.util.concurrent.TimeUnit

interface IAuthRepository {
    suspend fun sendSignUpRequest(user: UserData): BasicResponse
    suspend fun verifyOtp(phoneNumber: String, otp: String): VerifyOtpResponse
    suspend fun login(phoneNumber: String, password: String): LogInResponse
    suspend fun logout(): BasicResponse
    suspend fun sendResetPasswordRequest(phoneNumber: String, password: String): BasicResponse
    suspend fun verifyOtpResetPassword(phoneNumber: String, password: String, otp: String): BasicResponse
    suspend fun refreshToken(refreshToken: String): TokenResponse
    fun checkNeedToRefreshToken(): Boolean
    suspend fun updateFCMToken(fcmToken: String): BasicResponse
}

class AuthRepository(
    private val authApi: IAuthApi,
    private val userManager: UserManager
): IAuthRepository{
    override suspend fun sendSignUpRequest(user: UserData): BasicResponse {
        return authApi.sendSignUpRequest(user)
    }
    override suspend fun verifyOtp(phoneNumber: String, otp: String): VerifyOtpResponse {
        return authApi.verify(VerifyOtpRequest(phoneNumber, otp))
    }

    override suspend fun login(phoneNumber: String, password: String): LogInResponse {
        return authApi.login(LoginRequest(phoneNumber, password))
    }

    override suspend fun logout(): BasicResponse {
        return authApi.logout()
    }

    override suspend fun sendResetPasswordRequest(phoneNumber: String, password: String): BasicResponse {
        return authApi.sendResetPasswordRequest(ForgotPasswordRequest(phoneNumber, password))
    }

    override suspend fun verifyOtpResetPassword(
        phoneNumber: String,
        password: String,
        otp: String
    ):BasicResponse {
        return authApi.resetPassword(ResetPasswordRequest(phoneNumber, password, otp))
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse {
        return authApi.refreshToken(RefreshTokenRequest(refreshToken))
    }

    override fun checkNeedToRefreshToken(): Boolean {
        val refreshTokenExpire = userManager.getRefreshTokenExpires()
        val accessTokenExpire = userManager.getAccessTokenExpires()
        if ("" == refreshTokenExpire || "" == accessTokenExpire) {
            return true
        }

        val now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        if (accessTokenExpire.toLong() < now + Constants.TOKEN_NEAR_EXPIRED_TIME_IN_SECOND){
            Log.d("ERROR:", accessTokenExpire.toLong().toString() + " " + (now + Constants.TOKEN_NEAR_EXPIRED_TIME_IN_SECOND))
            Log.d("ERROR: ", "accessTokenExpire")
            return true
        }
        if (refreshTokenExpire.toLong() < now + Constants.TOKEN_NEAR_EXPIRED_TIME_IN_SECOND){
            Log.d("ERROR: ", "refreshTokenExpire")
            return true
        }
        return false
    }

    override suspend fun updateFCMToken(fcmToken: String): BasicResponse {
        return authApi.updateFCMToken(UpdateFCMTokenRequest(fcmToken))
    }


}