package vn.ztech.software.ecomSeller.ui.category

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UpdateCategoryResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.AddCategoryViewErrors
import vn.ztech.software.ecomSeller.ui.product.IListProductUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import java.io.File

class CategoryViewModel(private val listCategoriesUseCase: IListCategoriesUseCase, private val listProductsUseCase: IListProductUseCase): ViewModel() {
    val TAG = "CategoryViewModel"
    val currentSelectedCategory = MutableLiveData<Category>()

    private var _allCategories = MutableLiveData<MutableList<Category>>()
    val allCategories: LiveData<MutableList<Category>> get() = _allCategories
    val originalCategories = MutableLiveData<MutableList<Category>>()

    val isSearchCategoriesResultEmpty = MutableLiveData<Boolean>()
    val products = MutableLiveData<List<Product>>()

    private val _storeDataStatus = MutableLiveData<StoreDataStatus>()
    val storeDataStatus: LiveData<StoreDataStatus> get() = _storeDataStatus


    val uploadedImage = MutableLiveData<UploadImageResponse>()
    val createdCategory = MutableLiveData<Category>()
    val deleteCategoryStatus = MutableLiveData<Boolean>()
    val updatedCategory = MutableLiveData<UpdateCategoryResponse>()

    val error = MutableLiveData<CustomError>()
    val errorUI = MutableLiveData<AddCategoryViewErrors>()

    fun getCategories(){
        viewModelScope.launch {
            listCategoriesUseCase.getListCategories().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        _allCategories.value = it.data.toMutableList()
                        originalCategories.value = it.data.toMutableList()
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
            _allCategories.value = filteredCategories.toMutableList()
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
        errorUI.value = null
        deleteCategoryStatus.value = null
        uploadedImage.value = null
        createdCategory.value = null
    }
    fun uploadImage(file: File){
        Log.d("file", file.toString())
        viewModelScope.launch {
            listCategoriesUseCase.uploadImage(file).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        uploadedImage.value = it.data
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

    fun checkInputData(text: String, imgList: MutableList<Uri>) {
        val name = text.trim()
        if (name.isBlank() || imgList.isEmpty() || imgList[0].toString().isBlank()) {
            errorUI.value = AddCategoryViewErrors.EMPTY
        }else{
            if (name.length<5 || name.length>40){
                errorUI.value = AddCategoryViewErrors.NAME_LENGTH
            }else{
                var duplicatedName = false
                originalCategories.value?.forEach {
                    if(it.name.lowercase() == text.trim().lowercase()) duplicatedName = true
                }
                if(duplicatedName){
                    errorUI.value = AddCategoryViewErrors.DUPLICATED_NAME
                }else{
                    errorUI.value = AddCategoryViewErrors.NONE
                }
            }
        }
    }

    fun createCategory(createCategoryRequest: CreateCategoryRequest) {
        viewModelScope.launch {
            listCategoriesUseCase.crateCategory(createCategoryRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        createdCategory.value = it.data
                        originalCategories.value?.add(it.data)
                        _allCategories.value?.add(it.data)
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
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            listCategoriesUseCase.deleteCategory(categoryId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        deleteCategoryStatus.value = true
                        Log.d(TAG, "LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        deleteCategoryStatus.value = false
                        error.value = errorMessage(it.e)
                        Log.d(TAG +" ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }

    fun updateCategory(categoryId: String?, createCategoryRequest: CreateCategoryRequest) {
        if(categoryId==null) {
            errorMessage(CustomError(customMessage = "System error, category is empty! Try again later!"))
            return
        }
        viewModelScope.launch {
            listCategoriesUseCase.updateCategory(categoryId, createCategoryRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        updatedCategory.value = it.data
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
}