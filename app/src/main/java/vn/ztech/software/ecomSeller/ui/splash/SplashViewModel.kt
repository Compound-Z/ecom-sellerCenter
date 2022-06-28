package vn.ztech.software.ecomSeller.ui.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class SplashViewModel(private val useCase: ISplashUseCase): ViewModel() {
    val error = MutableLiveData<CustomError>()
    val tokenResponse = MutableLiveData<TokenResponse>()
    val page = MutableLiveData<ISplashUseCase.PAGE>()
    fun start() {
        viewModelScope.launch {
            useCase.nextPage().flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    is LoadState.Loading -> {
                        /**
                         * do nothing since this is the Splash Screen
                         * **/
                    }
                    is LoadState.Loaded -> {
                        page.value = it.data?:ISplashUseCase.PAGE.SIGNUP
                    }
                    is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }

    fun checkNeedToRefreshToken(): Boolean {
        return useCase.checkNeedToRefreshToken()
    }

    fun getToken() {
        Log.d("LOGIN", "SplashViewModel getToken")
        viewModelScope.launch {
            useCase.getToken().flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    is LoadState.Loading -> {
                        //何もしない
                    }
                    is LoadState.Loaded -> {
                        useCase.saveLogs(it.data.accessToken)
                        tokenResponse.value = it.data!!
                    }
                    is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
}