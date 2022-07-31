package vn.ztech.software.ecomSeller.util.extension

import java.text.NumberFormat
import kotlin.math.roundToInt

fun Float.round1Decimal(): Float{
    return (this * 10f).roundToInt() /10f
}
fun Int.toCurrency(): String{
    return "${NumberFormat.getNumberInstance().format(this)}đ"
}