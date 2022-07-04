package vn.ztech.software.ecomSeller.ui.home

import android.graphics.Point
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.GetOrderBaseOnTimeRequest
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderWithTime
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import java.time.LocalDate

class SaleReportViewModel(private val orderUseCase: IOrderUserCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val orders = MutableLiveData<MutableMap<Int,Map<LocalDate, List<OrderWithTime>>>>()
    val error = MutableLiveData<CustomError>()
    val indicator = MutableLiveData<String>()

    fun getOrdersBaseOnTime(numberOfDays: Int?, isLoadingEnabled: Boolean = true) {
        if (numberOfDays == null) {
            error.value = errorMessage(CustomError(customMessage = "System error"))
            return
        }
        viewModelScope.launch {
            orderUseCase.getOrdersBaseOnTime(GetOrderBaseOnTimeRequest(numberOfDays)).flowOn(
                Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        if(orders.value == null) orders.value = mutableMapOf()
                        val temp = orders.value
                        temp?.let {map->
                            map[numberOfDays] = it.data
                        }
                        orders.value = temp
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