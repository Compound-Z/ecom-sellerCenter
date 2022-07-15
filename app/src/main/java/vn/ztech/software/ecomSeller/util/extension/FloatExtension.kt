package vn.ztech.software.ecomSeller.util.extension

import kotlin.math.roundToInt

fun Float.round1Decimal(): Float{
    return (this * 10f).roundToInt() /10f
}