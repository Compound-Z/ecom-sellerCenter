package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import okhttp3.MultipartBody
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.request.SearchProductInCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UpdateCategoryResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product

@Keep
interface ICategoryApi{
    @GET("/api/v1/categories")
    suspend fun getListCategories(): List<Category>

    @GET("/api/v1/categories/{category}")
    suspend fun getListProductsInCategory(@Path("category")category: String): List<Product>

    @POST("/api/v1/categories/search/{category_name}")
    suspend fun search(@Path("category_name")searchWordsCategory: String, @Body request: SearchProductInCategoryRequest): List<Product>

    @Multipart
    @POST("/api/v1/categories/uploadImage")
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadImageResponse

    @POST("/api/v1/categories")
    suspend fun createCategory(@Body createCategoryRequest: CreateCategoryRequest): Category

    @DELETE("/api/v1/categories/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId")categoryId: String): BasicResponse
    @PATCH("/api/v1/categories/{categoryId}")
    suspend fun updateCategory(@Path("categoryId") categoryId: String, @Body createCategoryRequest: CreateCategoryRequest): UpdateCategoryResponse

}