package vn.ztech.software.ecomSeller.ui.cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.response.CartProductResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class CartViewModel(private val cartUseCase: ICartUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val products = MutableLiveData<MutableList<CartProductResponse>>()
    val addProductStatus = MutableLiveData<Boolean>()
    val deleteProductStatus = MutableLiveData<Boolean>()
    val adjustProductStatus = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError>()

    val priceData = MutableLiveData<PriceData>()

    fun addProductToCart(productId: String?, isLoadingEnabled: Boolean = true) {
        productId?:throw CustomError(customMessage = "System error")
        viewModelScope.launch {
            cartUseCase.addProductToCart(productId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        addProductStatus.value = true
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        addProductStatus.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }
    fun deleteProductFromCart(productId: String?) {
        productId?:throw CustomError(customMessage = "System error")
        viewModelScope.launch {
            cartUseCase.deleteProductFromCart(productId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                    }
                    is LoadState.Loaded -> {
                        //remove the product from cart in local memory
                        val deletedProductIdx = products.value?.indexOfFirst { it._id == productId }
                        deletedProductIdx?.let {products.value?.removeAt(deletedProductIdx)}

                        Log.d("PRODUCT", products.value.toString())
                        deleteProductStatus.value = true
                    }
                    is LoadState.Error -> {
                        deleteProductStatus.value = false
                        error.value = errorMessage(it.e)

                    }
                }
            }
        }
    }

    fun adjustProductQuantity(productId: String?, currQuantity: Int) {
        productId?:throw CustomError(customMessage = "System error")
        viewModelScope.launch {
            cartUseCase.adjustQuantityOfProductInCart(productId, currQuantity).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                    }
                    is LoadState.Loaded -> {
                        Log.d("PRODUCT", products.value.toString())
                        adjustProductStatus.value = true
                    }
                    is LoadState.Error -> {
                        adjustProductStatus.value = false
                        error.value = errorMessage(it.e)

                    }
                }
            }
        }
    }
    fun getListProductsInCart(isLoadingEnabled: Boolean = true){
        viewModelScope.launch {
            cartUseCase.getListProductsInCart().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        if(isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        updatePriceData(it.data.toMutableList())
                        loading.value = false
                        products.value = it.data.toMutableList()
                        Log.d("getListProductsInCart", products.value.toString())
                    }
                    is LoadState.Error -> {
                        loading.value = false
                        error.value = errorMessage(it.e)
                    }
                }
            }
        }
    }

    private fun updatePriceData(products: MutableList<CartProductResponse>?) {
        var sum = 0
        products?.let{
            it.forEach {
                sum+=it.price*it.quantity
            }
            priceData.value = PriceData(it.size, sum)
            Log.d("CartViewModel", priceData.value?.subTotal.toString())
        }
    }
    fun clearErrors() {
        error.value = null
    }

    inner class PriceData(
        var numberOfItem: Int,
        var subTotal: Int,
    )
}