package vn.ztech.software.ecomSeller.ui.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.repository.IAuthRepository

interface IAccountUseCase{
    fun logOut(): Flow<BasicResponse>
    fun clearLogs()
}

class AccountUseCase(private val authRepository: IAuthRepository, private val userManager: UserManager):
    IAccountUseCase {
    override fun logOut(): Flow<BasicResponse> = flow {
        emit(authRepository.logout())
    }
    override fun clearLogs() {
        userManager.clearLogs()
    }
}