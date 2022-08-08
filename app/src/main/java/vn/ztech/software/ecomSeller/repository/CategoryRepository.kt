package vn.ztech.software.ecomSeller.repository

import ProductInCategoryPagingSource
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import vn.ztech.software.ecomSeller.api.ICategoryApi
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.request.GetProductsInCategoryRequest
import vn.ztech.software.ecomSeller.api.request.GetProductsRequest
import vn.ztech.software.ecomSeller.api.request.SearchProductInCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UpdateCategoryResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import java.io.File

interface ICategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getListCategories(): List<Category>
    suspend fun getListProductsInCategory(category: String): Flow<PagingData<Product>>
    suspend fun search(searchWords: String, searchWordsProduct: String): Flow<PagingData<Product>>
    suspend fun uploadImage(file: File): UploadImageResponse
    suspend fun createCategory(createCategoryRequest: CreateCategoryRequest): Category
    suspend fun deleteCategory(categoryId: String): BasicResponse
    suspend fun updateCategory(categoryId: String, createCategoryRequest: CreateCategoryRequest): UpdateCategoryResponse
}

class CategoryRepository(private val CategoryApi: ICategoryApi): ICategoryRepository{
    override suspend fun getListCategories(): List<Category> {
        return CategoryApi.getListCategories()
    }
    override suspend fun getAllCategories(): List<Category> {
        return CategoryApi.getAllCategories()
    }

    override suspend fun getListProductsInCategory(category: String):  Flow<PagingData<Product>> {
        return Pager(
                config = PagingConfig(
                    pageSize = Constants.NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    ProductInCategoryPagingSource(category, GetProductsInCategoryRequest(), CategoryApi)
                },
                initialKey = 1
        ).flow
    }

    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<PagingData<Product>> {
        Log.d("x","search ${searchWordsCategory}")
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                SearchProductInCategoryPagingSource(searchWordsCategory, SearchProductInCategoryRequest(searchWordsProduct), CategoryApi)
            },
            initialKey = 1
        ).flow
    }

    override suspend fun uploadImage(file: File): UploadImageResponse {
        val requestFile: RequestBody = file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image",file.name, requestFile)
        return CategoryApi.uploadImage(body)
    }

    override suspend fun createCategory(createCategoryRequest: CreateCategoryRequest): Category {
        return CategoryApi.createCategory(createCategoryRequest)
    }

    override suspend fun deleteCategory(categoryId: String): BasicResponse {
        return CategoryApi.deleteCategory(categoryId)
    }

    override suspend fun updateCategory(categoryId: String, createCategoryRequest: CreateCategoryRequest): UpdateCategoryResponse {
        return CategoryApi.updateCategory(categoryId, createCategoryRequest)

    }

}