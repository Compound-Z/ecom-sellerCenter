package vn.ztech.software.ecomSeller.ui.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateOrderRequest
import vn.ztech.software.ecomSeller.api.request.GetOrderBaseOnTimeRequest
import vn.ztech.software.ecomSeller.api.request.UpdateOrderStatusBody
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails
import vn.ztech.software.ecomSeller.model.OrderWithTime
import vn.ztech.software.ecomSeller.repository.IOrderRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface IOrderUserCase{
    suspend fun createOrder(createOrderRequest: CreateOrderRequest): Flow<OrderDetails>
    suspend fun cancelOrder(orderId: String): Flow<OrderDetails>
    suspend fun getOrders(statusFilter: String): Flow<List<Order>>
    suspend fun getOrderDetails(orderId: String): Flow<OrderDetails>
    suspend fun updateOrderStatus(orderId: String, updateOrderStatusBody: UpdateOrderStatusBody): Flow<Order>
    suspend fun searchByOrderId(searchWords: String, statusFilter: String): Flow<List<Order>>
    suspend fun searchByUserName(searchWords: String, statusFilter: String): Flow<List<Order>>
    suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): Flow<Map<LocalDate, List<OrderWithTime>>>

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
        val orders2 = orders.sortedByDescending{ it.updatedAt }
        emit(orders2)
    }

    override suspend fun getOrderDetails(orderId: String): Flow<OrderDetails> = flow {
        val orderDetails = orderRepository.getOrderDetails(orderId)
        emit(orderDetails)
    }

    override suspend fun updateOrderStatus(_id: String, updateOrderStatusBody: UpdateOrderStatusBody): Flow<Order> =flow {
        val order = orderRepository.updateOrderStatus(_id, updateOrderStatusBody)
        emit(order)
    }

    override suspend fun searchByOrderId(searchWords: String, statusFilter: String, ): Flow<List<Order>> = flow {
        val orders = orderRepository.searchByOrderId(searchWords, statusFilter)
        val orders2 = orders.sortedByDescending{ it.updatedAt }
        emit(orders2)
    }
    override suspend fun searchByUserName(searchWords: String, statusFilter: String, ): Flow<List<Order>> = flow {
        val orders = orderRepository.searchByUserName(searchWords, statusFilter)
        val orders2 = orders.sortedByDescending{ it.updatedAt }
        emit(orders2)
    }

    override suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): Flow<Map<LocalDate, List<OrderWithTime>>> = flow {
        val orders = orderRepository.getOrdersBaseOnTime(getOrderBaseOnTimeRequest)
        val orders3 = orders.map {
            OrderWithTime(it,
            LocalDate.parse(it.createdAt.split("T").get(0)))
        }.groupBy {
            it.dateTime
        }
        emit(orders3)
    }
}