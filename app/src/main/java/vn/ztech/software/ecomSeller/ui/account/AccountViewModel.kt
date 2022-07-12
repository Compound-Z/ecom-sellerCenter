package vn.ztech.software.ecomSeller.ui.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class AccountViewModel(private val useCase: IAccountUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError?>()
    val isLogOutSuccessfully = MutableLiveData<Boolean>()

    //todo: cache user data
    fun logOut() {
        viewModelScope.launch {
            useCase.logOut().flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    is LoadState.Loading -> {
                        loading.value = true
                    }
                    is LoadState.Loaded -> {
                        clearLogs()
                        isLogOutSuccessfully.value = true
                        loading.value = false
                    }
                    is LoadState.Error -> {
//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }xxx
                        Log.d("LOGOUT:", "LoadState.Error ${it.e.message}")
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }

    private fun clearLogs() {
        useCase.clearLogs()
    }
    fun clearErrors() {
        error.value = null
    }
}