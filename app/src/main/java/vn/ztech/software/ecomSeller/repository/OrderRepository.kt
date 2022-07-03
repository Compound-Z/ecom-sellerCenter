package vn.ztech.software.ecomSeller.repository

import vn.ztech.software.ecomSeller.api.IOrderApi
import vn.ztech.software.ecomSeller.api.request.*
import vn.ztech.software.ecomSeller.api.response.GetOrdersRequest
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails

interface IOrderRepository{
    suspend fun createOrder(createOrderRequest: CreateOrderRequest): OrderDetails
    suspend fun cancelOrder(orderId: String): OrderDetails
    suspend fun getOrders(statusFilter: String): List<Order>
    suspend fun getOrderDetails(orderId: String): OrderDetails
    suspend fun updateOrderStatus(orderId: String, updateOrderStatusBody: UpdateOrderStatusBody): Order
    suspend fun searchByOrderId(searchWords: String, statusFilter: String): List<Order>
    suspend fun searchByUserName(searchWords: String, statusFilter: String): List<Order>
    suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): List<Order>
}

class OrderRepository(private val orderApi: IOrderApi): IOrderRepository{
    override suspend fun createOrder(createOrderRequest: CreateOrderRequest): OrderDetails {
        return orderApi.createOrder(createOrderRequest)
    }
    override suspend fun cancelOrder(orderId: String): OrderDetails {
        return orderApi.cancelOrder(orderId)
    }

    override suspend fun getOrders(statusFilter: String): List<Order> {
        return orderApi.getOrders(GetOrdersRequest(statusFilter))
    }

    override suspend fun getOrderDetails(orderId: String): OrderDetails {
        return orderApi.getOrderDetails(orderId)
    }

    override suspend fun updateOrderStatus(_id: String, updateOrderStatusBody: UpdateOrderStatusBody): Order {
        return orderApi.updateOrderStatus(_id, updateOrderStatusBody)
    }

    override suspend fun searchByOrderId(searchWords: String, statusFilter: String): List<Order> {
        return orderApi.searchByOrderId(SearchOrderByIdRequest(searchWords, statusFilter))
    }
    override suspend fun searchByUserName(searchWords: String, statusFilter: String): List<Order> {
        return orderApi.searchByUserName(SearchOrderByNameRequest(searchWords, statusFilter))
    }
    override suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): List<Order> {
        return orderApi.getOrdersBaseOnTime(getOrderBaseOnTimeRequest)
    }
}