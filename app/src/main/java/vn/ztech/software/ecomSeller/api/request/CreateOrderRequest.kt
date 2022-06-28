package vn.ztech.software.ecomSeller.api.request

data class CreateOrderRequest(
    val addressItemId: String,
    val note: String,
    val orderItems: List<CartItem>,
    val shippingServiceId: Int,
)
