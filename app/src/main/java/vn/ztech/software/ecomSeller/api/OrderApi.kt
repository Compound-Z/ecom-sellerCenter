package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.CreateOrderRequest
import vn.ztech.software.ecomSeller.api.response.GetOrdersRequest
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails

@Keep
interface IOrderApi{
    @POST("/api/v1/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDetails
    @DELETE("/api/v1/orders/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId: String): OrderDetails
    @POST("/api/v1/orders")
    suspend fun getOrders(@Body request: GetOrdersRequest): List<Order>
    @GET("/api/v1/orders/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): OrderDetails
}