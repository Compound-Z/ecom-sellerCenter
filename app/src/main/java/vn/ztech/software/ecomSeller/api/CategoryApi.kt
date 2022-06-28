package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import vn.ztech.software.ecomSeller.api.request.SearchProductInCategoryRequest
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

}