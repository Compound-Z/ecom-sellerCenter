package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import okhttp3.MultipartBody
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.ProductDetails

@Keep
interface IProductApi{
    @GET("/api/v1/products")
    suspend fun getListProducts(): List<Product>

    @GET("/api/v1/products/{id}")
    suspend fun getProductDetails(@Path("id")id: String): ProductDetails

    @GET("/api/v1/products/search/{searchWords}")
    suspend fun search(@Path("searchWords")searchWords: String): List<Product>

    @GET("/api/v1/products/origins")
    suspend fun getOrigins(): List<Country>
    @Multipart
    @POST("/api/v1/products/uploadImage")
    suspend fun uploadImage(@Part body: MultipartBody.Part): UploadImageResponse

    @POST("/api/v1/products")
    suspend fun createProduct(@Body createProductRequest: CreateProductRequest?): Product
}


