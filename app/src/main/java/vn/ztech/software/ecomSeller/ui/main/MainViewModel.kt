package vn.ztech.software.ecomSeller.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState

class MainViewModel(private val mainUseCase: IMainUseCase): ViewModel() {
    val updateFCMTokenStatus = MutableLiveData<Boolean>()
    fun updateFCMToken(fcmToken: String){
        viewModelScope.launch {
            mainUseCase.updateFCMToken(fcmToken).flowOn(Dispatchers.IO).toLoadState().collect{
                when (it) {
                    is LoadState.Loading -> {
                        Log.d("FCM_TOKEN","loading")

                        /**
                         * do nothing since this is the Splash Screen
                         * **/
                    }
                    is LoadState.Loaded -> {
                        updateFCMTokenStatus.value = true
                        Log.d("FCM_TOKEN","loaded: ${it.data}")
                    }
                    is LoadState.Error -> {
                        updateFCMTokenStatus.value = false

//                        if (it.e is TokenRefreshing) {
//                            return@collect
//                        }
                        Log.d("FCM_TOKEN","error: ${it.e.customMessage}")
                    }
                }
            }
        }
    }
}