package vn.ztech.software.ecomSeller.api.request

data class LoginRequest (
    val phoneNumber: String,
    val password: String
)