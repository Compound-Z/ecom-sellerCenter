package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import androidx.paging.PagingData
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.*
import vn.ztech.software.ecomSeller.api.request.GetOrdersRequest
import vn.ztech.software.ecomSeller.api.response.PagedGetAllOrdersResponse
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails

@Keep
interface IOrderApi{
    @POST("/api/v1/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDetails
    @DELETE("/api/v1/orders/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId: String): OrderDetails
    @POST("/api/v1/orders/my-orders")
    suspend fun getOrders(@Body request: GetOrdersRequest): PagedGetAllOrdersResponse
    @GET("/api/v1/orders/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): OrderDetails
    @PATCH("/api/v1/orders/{orderId}")
    suspend fun updateOrderStatus(@Path("orderId")_id: String, @Body request: UpdateOrderStatusBody): Order
    @POST("/api/v1/orders/search_by_order_id")
    suspend fun searchByOrderId(@Body searchOrderRequest: SearchOrderByIdRequest): PagedGetAllOrdersResponse
    @POST("/api/v1/orders/search_by_user_name")
    suspend fun searchByUserName(@Body searchOrderRequest: SearchOrderByNameRequest): PagedGetAllOrdersResponse
    @POST("/api/v1/orders/dashboard")
    suspend fun getOrdersBaseOnTime(@Body getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): List<Order>
}