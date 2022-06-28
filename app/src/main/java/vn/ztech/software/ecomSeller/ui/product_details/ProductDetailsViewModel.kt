package vn.ztech.software.ecomSeller.ui.product_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.ProductDetails
import vn.ztech.software.ecomSeller.domain.use_case.get_product_details.IProductDetailsUseCase;
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

private const val TAG = "ProductViewModel"

class ProductDetailsViewModel(
    private val productDetailsUseCase: IProductDetailsUseCase
    ) : ViewModel(){
    private val _productDetails = MutableLiveData<ProductDetails?>()
    val productDetails: LiveData<ProductDetails?> get() = _productDetails
    
    val product = MutableLiveData<Product>()
    private val _storeDataStatus = MutableLiveData<StoreDataStatus>()
    val storeDataStatus: LiveData<StoreDataStatus> get() = _storeDataStatus
    val error = MutableLiveData<CustomError>()

//    private val _errorStatus = MutableLiveData<List<AddItemErrors>>()
//    val errorStatus: LiveData<List<AddItemErrors>> get() = _errorStatus

    fun getProductDetails(id: String) {
        viewModelScope.launch {
            productDetailsUseCase.getProductDetails(id).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        _productDetails.value = it.data
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        _productDetails.value = null
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