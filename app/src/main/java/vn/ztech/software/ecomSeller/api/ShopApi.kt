package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.GET
import retrofit2.http.Path
import vn.ztech.software.ecomSeller.model.Shop

@Keep
interface IShopApi{
    @GET("/api/v1/shops/shop-info")
    suspend fun getShopInfo(): Shop

//    @POST("/api/v1/shops/{shop_id}/products") //todo:paginate
//    suspend fun getProductsInShop(@Path("shop_id") shopId: String, @Body request: GetProductsRequest): PagedGetAllProductsResponse
//
//    @GET("/api/v1/shops/{shop_id}/categories")
//    suspend fun getCategoriesInShop(@Path("shop_id") shopId: String): List<Category>
//
//    @POST("/api/v1/shops/{shop_id}/cate-products")
//    suspend fun getProductsOfCategoryInShop(@Path("shop_id") shopId: String, @Body request: GetProductsOfCategoryInShopRequest ): PagedGetAllProductsResponse
//
//    @POST("/api/v1/shops/search/{search_words}")
//    suspend fun searchProductsInShop(@Path("search_words") searchWords: String, @Body request: SearchProductsInShopRequest ): PagedGetAllProductsResponse
//
//    @POST("/api/v1/shops/search-cate/{search_words_product}")
//    suspend fun searchProductsOfCategoryInShop(@Path("search_words_product") searchWords: String, @Body request: SearchProductsOfCategoryInShopRequest ): PagedGetAllProductsResponse

}