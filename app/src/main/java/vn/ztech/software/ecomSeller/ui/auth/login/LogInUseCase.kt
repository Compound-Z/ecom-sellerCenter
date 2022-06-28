package vn.ztech.software.ecomSeller.ui.auth.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.LogInResponse
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.repository.IAuthRepository

interface ILogInUseCase{
    fun login(phoneNumber: String, password: String): Flow<LogInResponse>
    fun saveLogInInfo(userData: UserData, tokens: TokenResponse)
}

class LogInUseCase(private val authRepository: IAuthRepository, private val userManager: UserManager): ILogInUseCase {
    override fun login(phoneNumber: String, password: String): Flow<LogInResponse> = flow {
        emit(authRepository.login(phoneNumber, password))
    }

    override fun saveLogInInfo(userData: UserData, tokens: TokenResponse) {
        userManager.saveLogs(userData = userData, accessToken = tokens.accessToken, refreshToken = tokens.refreshToken )
    }
}