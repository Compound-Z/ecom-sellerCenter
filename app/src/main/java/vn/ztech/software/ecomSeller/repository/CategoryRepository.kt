package vn.ztech.software.ecomSeller.repository

import android.util.Log
import vn.ztech.software.ecomSeller.api.ICategoryApi
import vn.ztech.software.ecomSeller.api.request.SearchProductInCategoryRequest
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product

interface ICategoryRepository {
    suspend fun getListCategories(): List<Category>
    suspend fun getListProductsInCategory(category: String): List<Product>
    suspend fun search(searchWords: String, searchWordsProduct: String): List<Product>
}

class CategoryRepository(private val CategoryApi: ICategoryApi): ICategoryRepository{
    override suspend fun getListCategories(): List<Category> {
        return CategoryApi.getListCategories()
    }

    override suspend fun getListProductsInCategory(category: String): List<Product> {
        return CategoryApi.getListProductsInCategory(category)
    }

    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): List<Product> {
        Log.d("x","search ${searchWordsCategory}")
        return CategoryApi.search(searchWordsCategory, SearchProductInCategoryRequest(searchWordsProduct))
    }

}