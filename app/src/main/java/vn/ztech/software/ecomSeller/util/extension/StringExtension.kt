package vn.ztech.software.ecomSeller.util.extension

import vn.ztech.software.ecomSeller.model.Product

fun String.removeUnderline(): String{
    return this.replace('_', ' ').trim()
}

fun MutableList<String>.findIndexOf(category: String): Int{
    this.forEachIndexed{ idx, it ->
        if(it == category ) return idx
    }
    return -1
}