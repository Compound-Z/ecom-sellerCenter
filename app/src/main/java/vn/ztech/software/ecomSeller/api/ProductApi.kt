package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import androidx.paging.PagingData
import okhttp3.MultipartBody
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.request.GetProductsRequest
import vn.ztech.software.ecomSeller.api.request.QuickUpdateProductRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.PagedGetAllProductsResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.ProductDetails

@Keep
interface IProductApi{
    @POST("/api/v1/products/all")
    suspend fun getListProducts(@Body getProductsRequest: GetProductsRequest): PagedGetAllProductsResponse

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

    @PATCH("/api/v1/products/{productId}")
    suspend fun updateProduct(@Path("productId") productId: String, @Body createProductRequest: CreateProductRequest?): Product

    @PATCH("/api/v1/products/{productId}")
    suspend fun quickUpdateProduct(@Path("productId")productId: String, @Body request: QuickUpdateProductRequest): Product

    @DELETE("/api/v1/products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: String): BasicResponse
}


