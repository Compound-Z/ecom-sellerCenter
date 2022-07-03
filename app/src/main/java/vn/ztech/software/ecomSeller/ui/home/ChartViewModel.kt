package vn.ztech.software.ecomSeller.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.progressindicator.BaseProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.GetOrderBaseOnTimeRequest
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderWithTime
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ChartViewModel(private val orderUseCase: IOrderUserCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val orders = MutableLiveData<Map<LocalDate, List<OrderWithTime>>>()
    val error = MutableLiveData<CustomError>()
    val indicator = MutableLiveData<String>()
    val entries = MutableLiveData<List<Pair<Float, Float>>>()
    var maxY: Float = 0f
    var maxX: Float = 0f
    fun generateEntries(it: Map<LocalDate, List<OrderWithTime>>) {
        val entriesValue = mutableListOf<Pair<Float, Float>>()
        val now = LocalDate.now()

        it.forEach {entry->
            val yValue = getYValueBaseOnIndicator(entry.value, indicator.value?:"")
            if (yValue>maxY) maxY = yValue
            entriesValue.add(Pair(ChronoUnit.DAYS.between(entry.key, now).toFloat(), yValue))
        }
        maxX = it.size.toFloat()
        entries.value = entriesValue
    }

    private fun getYValueBaseOnIndicator(value: List<OrderWithTime>, indicator: String): Float {
        when(indicator){
            Constants.SaleReportIndicator[0]->{
                return value.size.toFloat()
            }
            Constants.SaleReportIndicator[1]->{
                val sales = value.sumOf{
                    it.order.billing.subTotal
                }
                return sales.toFloat()
            }
            Constants.SaleReportIndicator[2]->{
                val numberOfOrders = value.size
                val sales = value.sumOf{
                    it.order.billing.subTotal
                }
                return sales.toFloat()/numberOfOrders.toFloat()
            }
        }
        return 0f
    }

    fun clearErrors() {
        error.value = null
    }
}