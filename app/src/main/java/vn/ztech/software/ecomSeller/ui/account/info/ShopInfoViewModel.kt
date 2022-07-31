package vn.ztech.software.ecomSeller.ui.account.info

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Shop
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class ShopInfoViewModel(val shopUseCase: IShopInfoUseCase): ViewModel() {
    val shop = MutableLiveData<Shop>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<CustomError>()
    fun getShopInfo(showLoading: Boolean = true){
        viewModelScope.launch {
            shopUseCase.getShopInfo().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        loading.value = showLoading
                    }
                    is LoadState.Loaded -> {
                        loading.value = false
                        shop.value = it.data
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