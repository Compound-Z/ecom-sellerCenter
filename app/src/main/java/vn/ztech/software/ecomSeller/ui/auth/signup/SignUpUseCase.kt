package vn.ztech.software.ecomSeller.ui.auth.signup

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.repository.IAuthRepository

interface ISignUpUseCase{
    fun sendSignUpRequest(user: UserData): Flow<BasicResponse>
}
class SignUpUseCase(private val authRepository: IAuthRepository): ISignUpUseCase {
    override fun sendSignUpRequest(user: UserData): Flow<BasicResponse> = flow {
        val result = authRepository.sendSignUpRequest(user)
        Log.d(" authRepository.sendSignUpRequest(user)", result.message)
        emit(result)
    }
}