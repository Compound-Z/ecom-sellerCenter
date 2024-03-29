package vn.ztech.software.ecomSeller.ui.product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.request.ProductDetail
import vn.ztech.software.ecomSeller.api.request.QuickUpdateProductRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.LoadState
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.common.extension.toLoadState
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.AddProductViewErrors
import vn.ztech.software.ecomSeller.ui.category.IListCategoriesUseCase
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.errorMessage
import java.io.File

private const val TAG = "ProductViewModel"

class ProductViewModel(
    private val listProductsUseCase: IListProductUseCase,
    private val categoriesUseCase: IListCategoriesUseCase
): ViewModel() {
    var existed = false
    private val _storeDataStatus = MutableLiveData<StoreDataStatus>()
    val storeDataStatus: LiveData<StoreDataStatus> get() = _storeDataStatus

    private var _allProducts = MutableLiveData<PagingData<Product>>()
    val allProducts: LiveData<PagingData<Product>> get() = _allProducts
    val origins = MutableLiveData<List<Country>>()
    /**add product*/
    val currentSelectedProduct = MutableLiveData<Product>()
    val currentEditType = MutableLiveData<String>()
    val currentProductInput = MutableLiveData<CreateProductRequest>()
    val currentQuickProductInput = MutableLiveData<QuickUpdateProductRequest>()

    val currentSelectedOrigin = MutableLiveData<Country>()
    val uploadedImage = MutableLiveData<UploadImageResponse>()
    val createdProduct = MutableLiveData<Product>()
    val updatedProduct = MutableLiveData<Product>()
    val deletedProductStatus = MutableLiveData<BasicResponse>()

    /**category*/
    val currentSelectedCategory = MutableLiveData<Category>()

    val errorUI = MutableLiveData<AddProductViewErrors>()
    val error = MutableLiveData<CustomError>()


    fun getProducts(){
        viewModelScope.launch {
            var getList: Flow<PagingData<Product>> = if (currentSelectedCategory.value == null) listProductsUseCase.getListProducts()
                                                    else categoriesUseCase.getListProductsInCategory(currentSelectedCategory.value?.name?:"")
            getList.cachedIn(viewModelScope).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        _allProducts.value = it.data
                        Log.d(TAG, "LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG+" ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }

    fun search(searchWords: String){
        viewModelScope.launch {
            listProductsUseCase.search(searchWords).cachedIn(viewModelScope).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        _allProducts.value = it.data
                        Log.d(TAG, "SEARCH LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG+" SEARCH ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }

    fun getOrigins(){
        viewModelScope.launch {
            listProductsUseCase.getOrigins().flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        origins.value = it.data?: emptyList()
                        Log.d(TAG, "LOADED")
                    }
                    is LoadState.Error -> {
                        _storeDataStatus.value = StoreDataStatus.ERROR
                        error.value = errorMessage(it.e)
                        Log.d(TAG+" ERROR:", it.e.message.toString())
                    }
                }
            }
        }
    }

    fun clearErrors() {
        error.value = null
        errorUI.value = null
        createdProduct.value = null
        updatedProduct.value = null
        uploadedImage.value = null
    }
    fun clearSelected(){
        currentSelectedProduct.value = null
        currentSelectedOrigin.value = null
    }

    fun checkInputData(
        name: String,
        sku: String,
        price: String,
        imgList: List<Uri>,
        category: String,
        weight: String,
        stock: String,
        des: String,
        unit: String,
        brand: String,
        origin: String
    ) {
        if (name.trim().isEmpty() || sku.trim().isEmpty() || price.trim().isEmpty()
            || imgList.isEmpty() || category.trim().isEmpty() || weight.trim().isEmpty()
            || stock.trim().isEmpty() || des.trim().isEmpty() || unit.trim().isEmpty() || brand.trim().isEmpty() || origin.trim().isEmpty()){
            errorUI.value = AddProductViewErrors.EMPTY
            return
        }
        if (name.length < 5 || name.length > 100){
            errorUI.value = AddProductViewErrors.NAME
            return
        }
        val priceInt = price.toIntOrNull()
        if (priceInt == null || priceInt < 0){
            errorUI.value = AddProductViewErrors.PRICE
            return
        }
        val weightInt = weight.toIntOrNull()
        if (weightInt == null || weightInt < 0){
            errorUI.value = AddProductViewErrors.WEIGHT
            return
        }
        val stockInt = stock.toIntOrNull()
        if (stockInt == null || stockInt < 0){
            errorUI.value = AddProductViewErrors.STOCK
            return
        }
        if (des.length < 50 || des.length > 2500){
            errorUI.value = AddProductViewErrors.DES
            return
        }

        //create new product request instance
        currentProductInput.value = CreateProductRequest(
            product = vn.ztech.software.ecomSeller.api.request.Product(
                category = category,
                imageUrl = "",
                name = name,
                price = priceInt,
                sku = sku,
                stockNumber = stockInt,
                weight = weightInt
            ),
            productDetail = ProductDetail(
                brandName = brand,
                description = des,
                imageUrls = listOf(""),
                origin = origin,
                unit = unit
            )
        )
        errorUI.value = AddProductViewErrors.NONE
    }

    fun uploadImage(file: File){
        Log.d("file", file.toString())
        viewModelScope.launch {
            listProductsUseCase.uploadImage(file).flowOn(Dispatchers.IO).toLoadState().collect {
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

    fun createNewProduct(createProductRequest: CreateProductRequest?) {
        viewModelScope.launch {
            listProductsUseCase.createProduct(createProductRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        createdProduct.value = it.data
//                        _allProducts.value?.add(it.data) //todo : no need this line since after creating new product, when navigate back to the list products fragment, the app will call getProducts() automatically
                        Log.d(TAG, "LOADED"+ it.data.toString())
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

    fun updateProduct(productId: String?, createProductRequest: CreateProductRequest?) {
        if (productId==null) {
            error.value = errorMessage(CustomError(customMessage = "System error, product is empty!"))
            return
        }
        viewModelScope.launch {
            listProductsUseCase.updateProduct(productId, createProductRequest).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        updatedProduct.value = it.data
                        Log.d(TAG, "LOADED"+ it.data.toString())
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

    fun quickUpdate(productId: String?, request: QuickUpdateProductRequest?) {
        if (productId == null || request == null){
            error.value = errorMessage(CustomError(customMessage = "System error: product is empty"))
            return
        }
        viewModelScope.launch {
            listProductsUseCase.quickUpdateProduct(productId, request).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        updatedProduct.value = it.data
                        Log.d(TAG, "LOADED"+ it.data.toString())
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

    fun checkQuickUpdateInputData(price: String, stock: String) {
        if (price.trim().isEmpty() || stock.trim().isEmpty()){
            errorUI.value = AddProductViewErrors.EMPTY
            return
        }
        val priceInt = price.toIntOrNull()
        if (priceInt == null || priceInt < 0){
            errorUI.value = AddProductViewErrors.PRICE
            return
        }
        val stockInt = stock.toIntOrNull()
        if (stockInt == null || stockInt < 0){
            errorUI.value = AddProductViewErrors.STOCK
            return
        }

        //create new product request instance
        currentQuickProductInput.value = QuickUpdateProductRequest(
            product = vn.ztech.software.ecomSeller.api.request.QuickProduct(
                price = priceInt,
                stockNumber = stockInt,
            )
        )
        errorUI.value = AddProductViewErrors.NONE
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            listProductsUseCase.deleteProduct(productId).flowOn(Dispatchers.IO).toLoadState().collect {
                when(it){
                    LoadState.Loading -> {
                        _storeDataStatus.value = StoreDataStatus.LOADING
                    }
                    is LoadState.Loaded -> {
                        _storeDataStatus.value = StoreDataStatus.DONE
                        deletedProductStatus.value = it.data
                        Log.d(TAG, "LOADED"+ it.data.toString())
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
