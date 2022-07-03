package vn.ztech.software.ecomSeller.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.GetOrderBaseOnTimeRequest
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

private const val TAG = "HomeViewModel"

class HomeViewModel(private val orderUseCase: IOrderUserCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val orders = MutableLiveData<List<Order>>()
    val error = MutableLiveData<CustomError>()
    fun getOrdersBaseOnTime(numberOfDays: Int, isLoadingEnabled: Boolean = true) {
        viewModelScope.launch {
            orderUseCase.getOrdersBaseOnTime(GetOrderBaseOnTimeRequest(numberOfDays)).flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        orders.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
    fun clearErrors() {
        error.value = null
    }
}
