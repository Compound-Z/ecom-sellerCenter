package vn.ztech.software.ecomSeller.api.response

data class TokenResponse (
    val accessToken : Token,
    val refreshToken : Token,
)