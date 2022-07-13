package vn.ztech.software.ecomSeller.repository

import ProductPagingSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import vn.ztech.software.ecomSeller.api.IProductApi
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.request.GetProductsRequest
import vn.ztech.software.ecomSeller.api.request.QuickUpdateProductRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.ProductDetails
import java.io.File

interface IProductRepository {
    suspend fun getListProducts(): Flow<PagingData<Product>>
    suspend fun getProductDetails(productId: String): ProductDetails
    suspend fun search(searchWords: String): Flow<PagingData<Product>>
    suspend fun getOrigins(): List<Country>
    suspend fun uploadImage(file: File): UploadImageResponse
    suspend fun createProduct(createProductRequest: CreateProductRequest?): Product
    suspend fun updateProduct(productId: String, createProductRequest: CreateProductRequest?): Product
    suspend fun quickUpdateProduct(productId: String, request: QuickUpdateProductRequest): Product
    suspend fun deleteProduct(productId: String): BasicResponse
    suspend fun getOneProduct(productId: String): Product

}

class ProductRepository(private val productApi: IProductApi): IProductRepository{
    override suspend fun getListProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                ProductPagingSource(GetProductsRequest(),productApi)
            },
            initialKey = 1
        ).flow
    }

    override suspend fun getProductDetails(productId: String): ProductDetails {
        return productApi.getProductDetails(productId)
    }

    override suspend fun search(searchWords: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                SearchProductPagingSource(searchWords, GetProductsRequest(), productApi)
            },
            initialKey = 1
        ).flow
    }

    override suspend fun getOrigins(): List<Country> {
        return productApi.getOrigins()
    }
    override suspend fun uploadImage(file: File): UploadImageResponse {
        val requestFile: RequestBody = file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image",file.name, requestFile)
        return productApi.uploadImage(body)
    }

    override suspend fun createProduct(createProductRequest: CreateProductRequest?): Product {
        return productApi.createProduct(createProductRequest)
    }
    override suspend fun updateProduct(productId: String, createProductRequest: CreateProductRequest?): Product {
        return productApi.updateProduct(productId, createProductRequest)
    }

    override suspend fun quickUpdateProduct(
        productId: String,
        request: QuickUpdateProductRequest
    ): Product {
        return productApi.quickUpdateProduct(productId, request)
    }

    override suspend fun deleteProduct(productId: String): BasicResponse {
        return productApi.deleteProduct(productId)
    }

    override suspend fun getOneProduct(productId: String): Product {
        return productApi.getOneProduct(productId)
    }
}