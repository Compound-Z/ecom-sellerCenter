package vn.ztech.software.ecomSeller.util.extension

import vn.ztech.software.ecomSeller.model.OrderWithTime
import java.time.LocalDate

fun Map<LocalDate, List<OrderWithTime>>.getNumberOfOrder(): Int {
    var num = 0
    this.forEach{
        num += it.value.size
    }
    return num
}

fun Map<LocalDate, List<OrderWithTime>>.getTotalSales(): Int {
    var sum = 0
    this.forEach{
        it.value.forEach {
            sum+=it.order.billing.subTotal
        }
    }
    return sum
}
fun Map<LocalDate, List<OrderWithTime>>.getAvgSale(): Int {
    return this.getTotalSales() / this.getNumberOfOrder()
}