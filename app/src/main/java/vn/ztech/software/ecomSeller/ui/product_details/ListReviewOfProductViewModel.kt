package vn.ztech.software.ecomSeller.ui.product_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.ui.account.review.IReviewUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class ListReviewOfProductViewModel(val reviewUseCase: IReviewUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val reviews = MutableLiveData<PagingData<Review>>()
    val product = MutableLiveData<Product>()
    val error = MutableLiveData<CustomError>()
    fun getReviewsOfProduct(productId: String?, starFilter: Int?, isLoadingEnabled: Boolean = true) {
        if (productId == null) {
            error.value = errorMessage(CustomError(customMessage = "System error"))
            return
        }
        viewModelScope.launch {
            reviewUseCase.getListReviewOfAProduct(productId,starFilter).cachedIn(viewModelScope).flowOn(Dispatchers.IO).toLoadState().collect {
                when (it) {
                    LoadState.Loading -> {
                        if (isLoadingEnabled) loading.value = true
                    }
                    is LoadState.Loaded -> {
                        reviews.value = it.data
                        loading.value = false
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