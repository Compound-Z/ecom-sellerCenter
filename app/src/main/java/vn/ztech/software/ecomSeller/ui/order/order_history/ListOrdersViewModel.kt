package vn.ztech.software.ecomSeller.ui.order.order_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.UpdateOrderStatusBody
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class ListOrdersViewModel(private val orderUseCase: IOrderUserCase): ViewModel() {
    val currentSelectedOrder = MutableLiveData<Order>()
    val statusFilter = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val orders = MutableLiveData<List<Order>>()
    val order = MutableLiveData<Order>()
    val error = MutableLiveData<CustomError>()

    fun getOrders(statusFilter: String?, isLoadingEnabled: Boolean = true) {
        statusFilter ?: throw CustomError(customMessage = "System error")
        viewModelScope.launch {
            orderUseCase.getOrders(statusFilter).flowOn(Dispatchers.IO).toLoadState().collect {
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

    fun startProcessing(_orderId: String?, isLoadingEnabled: Boolean = true) {
        if (_orderId == null) {
            error.value = errorMessage(CustomError(customMessage = "System error: order is empty"))
            return
        }
        val targetStatus = "PROCESSING"
        updateOrderStatus(_orderId, targetStatus, isLoadingEnabled)
    }
    fun confirm(_orderId: String?, isLoadingEnabled: Boolean = true) {
        if (_orderId == null) {
            error.value = errorMessage(CustomError(customMessage = "System error: order is empty"))
            return
        }
        val targetStatus = "CONFIRMED"
        updateOrderStatus(_orderId, targetStatus, isLoadingEnabled)
    }
    private fun updateOrderStatus(_orderId: String, targetStatus: String, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            orderUseCase.updateOrderStatus(_orderId, UpdateOrderStatusBody(targetStatus)).flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        order.value = it.data
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