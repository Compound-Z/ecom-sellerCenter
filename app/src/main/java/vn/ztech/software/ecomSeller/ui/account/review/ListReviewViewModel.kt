package vn.ztech.software.ecomSeller.ui.account.review

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
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class ListReviewViewModel(val reviewUseCase: IReviewUseCase): ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val reviews = MutableLiveData<PagingData<Review>>()
    val error = MutableLiveData<CustomError>()
    fun getReviews(starFilter: Int?, isLoadingEnabled: Boolean = true) {
        viewModelScope.launch {
            reviewUseCase.getAllReview(starFilter).cachedIn(viewModelScope).flowOn(Dispatchers.IO).toLoadState().collect {
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