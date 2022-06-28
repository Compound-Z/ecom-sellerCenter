package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.GetShippingOptionsReq
import vn.ztech.software.ecomSeller.api.response.ShippingOption

@Keep
interface IShippingApi{
    @POST("/api/v1/orders/shipping-fee")
    suspend fun getShippingOptions(@Body request: GetShippingOptionsReq): List<ShippingOption>

}