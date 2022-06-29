package vn.ztech.software.ecomSeller.util.extension

fun String.removeUnderline(): String{
    return this.replace('_', ' ').trim()
}