package vn.ztech.software.ecomSeller.ui.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateOrderRequest
import vn.ztech.software.ecomSeller.api.request.UpdateOrderStatusBody
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails
import vn.ztech.software.ecomSeller.repository.IOrderRepository

interface IOrderUserCase{
    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Flow<OrderDetails>
    suspend fun cancelOrder(orderId: String): Flow<OrderDetails>
    suspend fun getOrders(statusFilter: String): Flow<List<Order>>
    suspend fun getOrderDetails(orderId: String): Flow<OrderDetails>
    suspend fun updateOrderStatus(orderId: String, updateOrderStatusBody: UpdateOrderStatusBody): Flow<Order>

}

class OrderUseCase(private val orderRepository: IOrderRepository): IOrderUserCase{

    override suspend fun createOrder(createOrderRequest: CreateOrderRequest): Flow<OrderDetails> = flow {
        val order = orderRepository.createOrder(createOrderRequest)
        emit(order)
    }

    override suspend fun cancelOrder(orderId: String): Flow<OrderDetails> = flow {
        val orderDetails = orderRepository.cancelOrder(orderId)
        emit(orderDetails)
    }

    override suspend fun getOrders(statusFilter: String): Flow<List<Order>> = flow {
        val orders = orderRepository.getOrders(statusFilter)
        emit(orders)
    }

    override suspend fun getOrderDetails(orderId: String): Flow<OrderDetails> = flow {
        val orderDetails = orderRepository.getOrderDetails(orderId)
        emit(orderDetails)
    }

    override suspend fun updateOrderStatus(_id: String, updateOrderStatusBody: UpdateOrderStatusBody): Flow<Order> =flow {
        val order = orderRepository.updateOrderStatus(_id, updateOrderStatusBody)
        emit(order)
    }
}