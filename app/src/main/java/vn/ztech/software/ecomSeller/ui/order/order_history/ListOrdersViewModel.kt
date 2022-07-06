package vn.ztech.software.ecomSeller.ui.order.order_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
    val listSearchCriteria = MutableLiveData<List<String>>()
    val currentSelectedSearchCriteria = MutableLiveData<String>()
    val currentSelectedOrder = MutableLiveData<Order>()
    val statusFilter = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val orders = MutableLiveData<PagingData<Order>>()
    val order = MutableLiveData<Order>()
    val error = MutableLiveData<CustomError>()

    fun getOrders(statusFilter: String?, isLoadingEnabled: Boolean = true) {
        statusFilter ?: throw CustomError(customMessage = "System error")
        viewModelScope.launch {
            orderUseCase.getOrders(statusFilter).flowOn(Dispatchers.IO).cachedIn(viewModelScope).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        orders.value = it.data
//                        loading.value = false
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

    fun search(searchWords: String, isLoadingEnabled: Boolean = true) {
        if (statusFilter.value == null || currentSelectedSearchCriteria.value == null){
            error.value = errorMessage(CustomError(customMessage = "System error"))
            return
        }
        if (currentSelectedSearchCriteria.value == listSearchCriteria.value?.get(0)){
            searchByOrderId(searchWords, statusFilter, isLoadingEnabled)
        }else{
            searchByUserName(searchWords, statusFilter, isLoadingEnabled)
        }
    }

    private fun searchByUserName(searchWords: String, statusFilter: MutableLiveData<String>, isLoadingEnabled: Boolean = true) {
        viewModelScope.launch {
            orderUseCase.searchByUserName(searchWords, statusFilter.value?:"").flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
//                        orders.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }

    private fun searchByOrderId(searchWords: String, statusFilter: MutableLiveData<String>, isLoadingEnabled: Boolean = true) {
        viewModelScope.launch {
            orderUseCase.searchByOrderId(searchWords, statusFilter.value?:"").flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
//                        orders.value = it.data
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
}