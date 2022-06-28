package vn.ztech.software.ecomSeller.ui.order.order

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.CreateOrderRequest
import vn.ztech.software.ecomSeller.api.request.GetShippingOptionsReq
import vn.ztech.software.ecomSeller.api.response.CartProductResponse
import vn.ztech.software.ecomSeller.api.response.ShippingOption
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.*
import vn.ztech.software.ecomSeller.ui.order.IOrderUserCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import vn.ztech.software.ecomSeller.util.extension.toCartItems

private const val TAG = "OrderViewModel"
class OrderViewModel(private val shippingUseCase: IShippingUserCase, val orderUseCase: IOrderUserCase): ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError>()
    val products = MutableLiveData<MutableList<CartProductResponse>>()
    val currentSelectedAddress = MutableLiveData<AddressItem>()
    val shippingOptions = MutableLiveData<List<ShippingOption>>()
    val currentSelectedShippingOption = MutableLiveData<ShippingOption>()
    val loadingShipping = MutableLiveData<Boolean>()
    val orderCost = MutableLiveData<OrderCost>()
    val createdOrder = MutableLiveData<OrderDetails>()

    fun getShippingOptions(getShippingOptionReq: GetShippingOptionsReq, isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            shippingUseCase.getShippingOptions(getShippingOptionReq).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loadingShipping.value = true
                    }
                    is LoadState.Loaded -> {
                        loadingShipping.value = false
                        shippingOptions.value = it.data
                        Log.d("getShippingOptions", shippingOptions.value.toString())
                    }
                    is LoadState.Error -> {
                        loadingShipping.value = false
                        error.value = errorMessage(it.e)
                        Log.d("getShippingOptions: error", it.e.message.toString())

                    }
                }
            }
        }
    }

    fun checkIfCanGetShippingOptions(): Boolean {
        Log.d(TAG, "${!products.value.isNullOrEmpty()} && ${currentSelectedAddress.value != null}")
        return !products.value.isNullOrEmpty() && currentSelectedAddress.value != null
    }

    fun calculateCost(){
        val productsCost = products.value?.sumOf { it.price*it.quantity }?:-1
        val shippingFee = currentSelectedShippingOption.value?.fee?.total?:-1
        orderCost.value = OrderCost(productsCost, shippingFee, productsCost+shippingFee)
    }

    fun createOrder(products: MutableList<CartProductResponse>?, addressItem: AddressItem?, shippingOption: ShippingOption?) {
        if (products.isNullOrEmpty() || addressItem == null || shippingOption == null){
            if(addressItem==null){
                error.value = errorMessage(CustomError(customMessage = "Please choose an address"))
            }else if(shippingOption == null){
                error.value = errorMessage(CustomError(customMessage = "Please choose a shipping option"))
            }else{
                error.value = errorMessage(CustomError(customMessage = "Products is empty, go shopping please"))
            }
        }else{
            val createOrderRequest = CreateOrderRequest(
                addressItemId = addressItem._id,
                orderItems = products.toCartItems(),
                shippingServiceId = shippingOption.service_id,
                note = "" //todo: implement ui for Note
            )
            viewModelScope.launch {
                orderUseCase.createOrder(createOrderRequest).flowOn(Dispatchers.IO).toLoadState().collect{
                    when(it){
                        LoadState.Loading -> {
                            loading.value = true
                        }
                        is LoadState.Loaded -> {
                            loading.value = false
                            createdOrder.value = it.data
                            Log.d("createOrder", createdOrder.value.toString())
                        }
                        is LoadState.Error -> {
                            loading.value = false
                            error.value = errorMessage(it.e)
                            Log.d("createOrder: error", it.e.message.toString())
                        }
                    }
                }
            }
        }
    }


    data class OrderCost(
        var productsCost: Int = -1,
        var shippingFee: Int = -1,
        var totalCost: Int = -1,
    )
    fun clearErrors() {
        error.value = null
    }
}