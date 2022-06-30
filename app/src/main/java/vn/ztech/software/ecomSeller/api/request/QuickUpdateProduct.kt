package vn.ztech.software.ecomSeller.api.request

class QuickUpdateProductRequest (
    val product: QuickProduct
)
class QuickProduct (
    val price: Int,
    val stockNumber: Int,
)