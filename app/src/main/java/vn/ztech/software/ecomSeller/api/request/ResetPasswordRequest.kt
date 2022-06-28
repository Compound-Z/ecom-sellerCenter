package vn.ztech.software.ecomSeller.api.request

data class ResetPasswordRequest(
    val phoneNumber: String,
    val password: String,
    val otp: String
)