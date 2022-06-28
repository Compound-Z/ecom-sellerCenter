package vn.ztech.software.ecomSeller.ui.category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.repository.ICategoryRepository

interface IListCategoriesUseCase{
    suspend fun getListCategories(): Flow<List<Category>>
    suspend fun getListProductsInCategory(category: String): Flow<List<Product>>
    suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>>
}

class ListCategoriesUseCase(private val categoryRepository: ICategoryRepository): IListCategoriesUseCase{
    override suspend fun getListCategories(): Flow<List<Category>> = flow{
        val listCategories = categoryRepository.getListCategories()
        emit(listCategories)
    }
    override suspend fun getListProductsInCategory(category: String): Flow<List<Product>> = flow {
        emit(categoryRepository.getListProductsInCategory(category))
    }
    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>> = flow{
        emit(categoryRepository.search(searchWordsCategory, searchWordsProduct))
    }
}