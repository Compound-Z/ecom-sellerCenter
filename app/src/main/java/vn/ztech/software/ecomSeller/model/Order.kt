package vn.ztech.software.ecomSeller.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime


/**this is a minimal version of Order response from getMyOrder api*/
data class Order(
    val _id: String,
    val orderId: String,
    val user: UserOrder,
    val billing: Billing,
    val orderItems: List<OrderItem>,
    val status: String,
    val updatedAt: String,
    val createdAt: String,
)

data class OrderWithTime(
    val order: Order,
    val dateTime: LocalDate
)