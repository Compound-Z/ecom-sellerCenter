package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.AddProductToCartRequest
import vn.ztech.software.ecomSeller.api.request.AdjustProductQuantityRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.CartProductResponse

@Keep
interface ICartApi{
    @GET("/api/v1/cart")
    suspend fun getListProductsInCart(): List<CartProductResponse>

    @POST("/api/v1/cart")
    suspend fun addProductToCart(@Body request: AddProductToCartRequest): BasicResponse

    @PATCH("/api/v1/cart/{productId}")
    suspend fun adjustProductQuantityInCart(@Path("productId")productId: String, @Body request: AdjustProductQuantityRequest): BasicResponse

    @DELETE("/api/v1/cart/{productId}")
    suspend fun deleteProductFromCart(@Path("productId") productId: String): BasicResponse
}