package vn.ztech.software.ecomSeller.repository

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import vn.ztech.software.ecomSeller.api.ICategoryApi
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.request.SearchProductInCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import java.io.File

interface ICategoryRepository {
    suspend fun getListCategories(): List<Category>
    suspend fun getListProductsInCategory(category: String): List<Product>
    suspend fun search(searchWords: String, searchWordsProduct: String): List<Product>
    suspend fun uploadImage(file: File): UploadImageResponse
    suspend fun createCategory(createCategoryRequest: CreateCategoryRequest): Category
    suspend fun deleteCategory(categoryId: String): BasicResponse

}

class CategoryRepository(private val CategoryApi: ICategoryApi): ICategoryRepository{
    override suspend fun getListCategories(): List<Category> {
        return CategoryApi.getListCategories()
    }

    override suspend fun getListProductsInCategory(category: String): List<Product> {
        return CategoryApi.getListProductsInCategory(category)
    }

    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): List<Product> {
        Log.d("x","search ${searchWordsCategory}")
        return CategoryApi.search(searchWordsCategory, SearchProductInCategoryRequest(searchWordsProduct))
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

}