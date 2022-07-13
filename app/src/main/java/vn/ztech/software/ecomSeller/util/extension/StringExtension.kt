package vn.ztech.software.ecomSeller.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.removeUnderline(): String{
    return this.replace('_', ' ').trim()
}

fun MutableList<String>.findIndexOf(category: String): Int{
    this.forEachIndexed{ idx, it ->
        if(it == category ) return idx
    }
    return -1
}

fun String.toDateTimeString(): String {
    val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}

fun String.getFirstNumber(): Int? {
    val firstNumString = this.split(" ")[0]
    var num:Int? =  null
    return try {
        num = firstNumString.toInt()
        num
    }catch (e: Exception){
        null
    }
}