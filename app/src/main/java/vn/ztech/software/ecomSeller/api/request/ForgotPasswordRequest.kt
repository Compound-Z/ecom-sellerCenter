package vn.ztech.software.ecomSeller.api.request

data class ForgotPasswordRequest(
    val phoneNumber: String,
    val password: String
)
