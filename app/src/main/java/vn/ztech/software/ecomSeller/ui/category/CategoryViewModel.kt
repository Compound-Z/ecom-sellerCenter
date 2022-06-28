package vn.ztech.software.ecomSeller.ui.category

import android.util.Log
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
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.home.IListProductUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage

class CategoryViewModel(private val listCategoriesUseCase: IListCategoriesUseCase, private val listProductsUseCase: IListProductUseCase): ViewModel() {
    val TAG = "CategoryViewModel"
    val currentSelectedCategory = MutableLiveData<Category>()
    private var _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>> get() = _allCategories
    val originalCategories = MutableLiveData<List<Category>>()
    val isSearchCategoriesResultEmpty = MutableLiveData<Boolean>()
    val products = MutableLiveData<List<Product>>()

    private val _storeDataStatus = MutableLiveData<StoreDataStatus>()
    val storeDataStatus: LiveData<StoreDataStatus> get() = _storeDataStatus

    val error = MutableLiveData<CustomError>()


    fun getCategories(){
        viewModelScope.launch {
            listCategoriesUseCase.getListCategories().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        _allCategories.value = it.data?: emptyList()
                        originalCategories.value = it.data?: emptyList()
                        Log.d(TAG, "LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG +" ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }
    fun getProductsInCategory(){
        viewModelScope.launch {
            listCategoriesUseCase.getListProductsInCategory(currentSelectedCategory.value?.name?:"").flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        products.value = it.data?: emptyList()
                        Log.d(TAG, "LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG +" ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }
    fun searchProducts(searchWords: String){
        viewModelScope.launch {
            listProductsUseCase.search(searchWords).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        products.value = it.data?: emptyList()
                        Log.d(TAG, "SEARCH LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG +" SEARCH ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }
    fun searchCategoriesLocal(searchWords: String){
        val filteredCategories = originalCategories.value?.filter { category ->
            category.name.contains(searchWords, ignoreCase = true)
        }
        if (filteredCategories.isNullOrEmpty()){
            isSearchCategoriesResultEmpty.value = true
        }else{
            _allCategories.value = filteredCategories
            isSearchCategoriesResultEmpty.value = false
        }
    }
    fun searchProductsInCategory(searchWordsProduct: String){
        Log.d("searchProductsInCategory", searchWordsProduct + currentSelectedCategory.value?.name?:"")
        viewModelScope.launch {
            listCategoriesUseCase.search(currentSelectedCategory.value?.name?:"", searchWordsProduct).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        products.value = it.data?: emptyList()
                        Log.d(TAG, "SEARCH LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG +" SEARCH ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }
    fun clearErrors() {
        error.value = null
        isSearchCategoriesResultEmpty.value = null
    }
}