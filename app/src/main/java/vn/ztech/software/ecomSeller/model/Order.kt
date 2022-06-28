package vn.ztech.software.ecomSeller.model

/**this is a minimal version of Order response from getMyOrder api*/
data class Order(
    val _id: String,
    val billing: Billing,
    val orderItems: List<OrderItem>,
    val status: String
)