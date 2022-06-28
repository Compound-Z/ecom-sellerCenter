package vn.ztech.software.ecomSeller.ui.auth.forgot_password

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.repository.IAuthRepository

interface IResetPasswordUseCase{
    fun sendResetPasswordRequest(phoneNumber: String, password: String): Flow<BasicResponse>
}

class ResetPasswordUseCase(private val authRepository: IAuthRepository): IResetPasswordUseCase {
    override fun sendResetPasswordRequest(phoneNumber: String, password: String): Flow<BasicResponse> = flow {
        emit(authRepository.sendResetPasswordRequest(phoneNumber, password))
    }
}