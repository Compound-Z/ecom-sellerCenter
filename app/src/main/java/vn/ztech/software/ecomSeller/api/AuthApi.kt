package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import vn.ztech.software.ecomSeller.api.request.*
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.LogInResponse
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.api.response.VerifyOtpResponse
import vn.ztech.software.ecomSeller.model.UserData

@Keep
interface IAuthApi{
    @POST("/api/v1/auth/signup")
    suspend fun sendSignUpRequest(@Body request: UserData): BasicResponse

    @POST("/api/v1/auth/verify-otp")
    suspend fun verify(@Body request: VerifyOtpRequest): VerifyOtpResponse

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LogInResponse

    @DELETE("/api/v1/auth/logout")
    suspend fun logout(): BasicResponse

    @POST("/api/v1/auth/forgot-password")
    suspend fun sendResetPasswordRequest(@Body request: ForgotPasswordRequest): BasicResponse

    @POST("/api/v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): BasicResponse

    @POST("/api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @POST("/api/v1/auth/update-fcm-token")
    suspend fun updateFCMToken(@Body request: UpdateFCMTokenRequest): BasicResponse


}