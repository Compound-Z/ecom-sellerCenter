package vn.ztech.software.ecomSeller.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import vn.ztech.software.ecomSeller.api.IOrderApi
import vn.ztech.software.ecomSeller.api.request.*
import vn.ztech.software.ecomSeller.api.request.GetOrdersRequest
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.model.Order
import vn.ztech.software.ecomSeller.model.OrderDetails

interface IOrderRepository{
    suspend fun createOrder(createOrderRequest: CreateOrderRequest): OrderDetails
    suspend fun cancelOrder(orderId: String): OrderDetails
    suspend fun getOrders(statusFilter: String): Flow<PagingData<Order>>
    suspend fun getOrderDetails(orderId: String): OrderDetails
    suspend fun updateOrderStatus(orderId: String, updateOrderStatusBody: UpdateOrderStatusBody): Order
    suspend fun searchByOrderId(searchWords: String, statusFilter: String): Flow<PagingData<Order>>
    suspend fun searchByUserName(searchWords: String, statusFilter: String): Flow<PagingData<Order>>
    suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): List<Order>
}

class OrderRepository(private val orderApi: IOrderApi): IOrderRepository{
    override suspend fun createOrder(createOrderRequest: CreateOrderRequest): OrderDetails {
        return orderApi.createOrder(createOrderRequest)
    }
    override suspend fun cancelOrder(orderId: String): OrderDetails {
        return orderApi.cancelOrder(orderId)
    }

    override suspend fun getOrders(statusFilter: String): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                OrderPagingSource(GetOrdersRequest(statusFilter),orderApi)
            },
            initialKey = 1
        ).flow
    }

    override suspend fun getOrderDetails(orderId: String): OrderDetails {
        return orderApi.getOrderDetails(orderId)
    }

    override suspend fun updateOrderStatus(_id: String, updateOrderStatusBody: UpdateOrderStatusBody): Order {
        return orderApi.updateOrderStatus(_id, updateOrderStatusBody)
    }

    override suspend fun searchByOrderId(searchWords: String, statusFilter: String): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                SearchOrderByIdPagingSource(SearchOrderByIdRequest(searchWords, statusFilter),orderApi)
            },
            initialKey = 1
        ).flow
    }
    override suspend fun searchByUserName(searchWords: String, statusFilter: String): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                SearchOrderByNamePagingSource(SearchOrderByNameRequest(searchWords, statusFilter),orderApi)
            },
            initialKey = 1
        ).flow
    }
    override suspend fun getOrdersBaseOnTime(getOrderBaseOnTimeRequest: GetOrderBaseOnTimeRequest): List<Order> {
        return orderApi.getOrdersBaseOnTime(getOrderBaseOnTimeRequest)
    }
}