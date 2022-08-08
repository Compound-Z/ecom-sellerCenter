package vn.ztech.software.ecomSeller.repository

import vn.ztech.software.ecomSeller.api.IShopApi
import vn.ztech.software.ecomSeller.model.Shop

interface IShopRepository {
    suspend fun getShopInfo(): Shop
//    suspend fun getListProductsInShop(shopId: String): Flow<PagingData<Product>>
//    suspend fun searchListProductsInShop(shopId: String, searchWords: String): Flow<PagingData<Product>>
//    suspend fun getListCategoriesInShop(shopId: String): List<Category>
//    suspend fun getListProductsOfCategoryInShop(shopId: String, categoryName: String): Flow<PagingData<Product>>
//    suspend fun searchListProductsOfCategoryInShop(shopId: String, searchWordsCategory: String, searchWordsProduct: String): Flow<PagingData<Product>>

}

class ShopRepository(private val shopApi: IShopApi): IShopRepository{

    override suspend fun getShopInfo(): Shop {
        return shopApi.getShopInfo()
    }
//    override suspend fun getListProductsInShop(shopId: String): Flow<PagingData<Product>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.NETWORK_PAGE_SIZE,
//                enablePlaceholders = false,
//            ),
//            pagingSourceFactory = {
//                ProductsInShopPagingSource(shopId, GetProductsRequest(),shopApi)
//            },
//            initialKey = 1
//        ).flow
//    }
//
//    override suspend fun searchListProductsInShop(shopId: String, searchWords: String): Flow<PagingData<Product>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.NETWORK_PAGE_SIZE,
//                enablePlaceholders = false,
//            ),
//            pagingSourceFactory = {
//                SearchProductsInShopPagingSource(searchWords, SearchProductsInShopRequest(shopId=shopId),shopApi)
//            },
//            initialKey = 1
//        ).flow
//    }
//
//    override suspend fun getListCategoriesInShop(shopId: String): List<Category> {
//        return shopApi.getCategoriesInShop(shopId)
//    }
//
//    override suspend fun getListProductsOfCategoryInShop(shopId: String, categoryName: String): Flow<PagingData<Product>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.NETWORK_PAGE_SIZE,
//                enablePlaceholders = false,
//            ),
//            pagingSourceFactory = {
//                ProductsOfCategoryInShopPagingSource(shopId, GetProductsOfCategoryInShopRequest(categoryName = categoryName),shopApi)
//            },
//            initialKey = 1
//        ).flow
//    }
//    override suspend fun searchListProductsOfCategoryInShop(shopId: String, searchWordsCategory: String, searchWordsProduct: String): Flow<PagingData<Product>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = Constants.NETWORK_PAGE_SIZE,
//                enablePlaceholders = false,
//            ),
//            pagingSourceFactory = {
//                SearchProductsOfCategoryInShopPagingSource(searchWordsProduct, SearchProductsOfCategoryInShopRequest(shopId=shopId, searchWordsCategory = searchWordsCategory),shopApi)
//            },
//            initialKey = 1
//        ).flow
//    }

}