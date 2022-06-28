package vn.ztech.software.ecomSeller.api.response

import vn.ztech.software.ecomSeller.model.UserData

data class LogInResponse (
    val user: UserData,
    val tokens: TokenResponse
)