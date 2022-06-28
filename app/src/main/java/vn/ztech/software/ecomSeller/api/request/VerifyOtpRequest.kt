package vn.ztech.software.ecomSeller.api.request

data class VerifyOtpRequest (
    val phoneNumber: String,
    val otp: String
)