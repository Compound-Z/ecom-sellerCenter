package vn.ztech.software.ecomSeller.common

object Constants {

    val BASE_URL = "https://ecom-z.herokuapp.com"
    val BASE_URL_DEBUG = "http://192.168.0.103:5000"

    fun getBaseUrl(): String{
        return BASE_URL_DEBUG
    }

    val TOKEN_NEAR_EXPIRED_TIME_IN_SECOND = 5*60 /**five minutes, consider the internet delay time*/

    const val VERIFY_FAILED = "pending"
    const val VERIFY_APPROVED = "approved"

    val OrderStatus = listOf<String>(
        "", //empty string for no filter query, that means get all data
        "PENDING",
        "PROCESSING",
        "CONFIRMED",
        "CANCELED",
        "RECEIVED",
    )

    val SaleReportTimeOptions = arrayOf(
        Pair("1 day", 1),
        Pair("7 days", 7),
        Pair("30 days", 30)
    )
    val StatusFilterToAction = mapOf<String, String>(
        "" to "View Details",
        "PENDING" to "Start Processing",
        "PROCESSING" to "Confirm",
        "CONFIRMED" to "View Details",
        "CANCELED" to "View Details",
        "RECEIVED" to "View Details"
    )
    val SaleReportIndicator = listOf<String>(
        "Order number",
        "Sales",
        "Average sales",
    )

    const val NETWORK_PAGE_SIZE = 10
    const val REVIEW_PREVIEW_PAGE_SIZE = 3
    const val CHANNEL_ID = "666"
    const val MAX_FILE_SIZE = 4096 //4M

}