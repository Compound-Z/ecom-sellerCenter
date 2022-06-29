package vn.ztech.software.ecomSeller.ui.category

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.repository.ICategoryRepository
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.removeUnderline
import java.io.File

interface IListCategoriesUseCase{
    suspend fun getListCategories(): Flow<List<Category>>
    suspend fun getListProductsInCategory(category: String): Flow<List<Product>>
    suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>>
    fun uploadImage(file: File): Flow<UploadImageResponse>
    fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category>
    fun deleteCategory(categoryId: String): Flow<BasicResponse>

}

class ListCategoriesUseCase(private val categoryRepository: ICategoryRepository): IListCategoriesUseCase{
    override suspend fun getListCategories(): Flow<List<Category>> = flow{
        val listCategories = categoryRepository.getListCategories()
        listCategories.forEach() {
            it.name = it.name.removeUnderline()
        }
        emit(listCategories)
    }
    override suspend fun getListProductsInCategory(category: String): Flow<List<Product>> = flow {
        emit(categoryRepository.getListProductsInCategory(category))
    }
    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>> = flow{
        emit(categoryRepository.search(searchWordsCategory, searchWordsProduct))
    }

    override fun uploadImage(file: File): Flow<UploadImageResponse> = flow{
        if(!"jpg|png|jpeg".contains(file.extension)) throw CustomError(customMessage = "Wrong file type, please submit image")
        if (file.length()/1024>2048) throw CustomError(customMessage = "File is too large, please submit file smaller than 2M")
        else emit(categoryRepository.uploadImage(file))
    }

    override fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category> = flow {
        val category = categoryRepository.createCategory(createCategoryRequest)
        category.name = category.name.removeUnderline()
        emit(category)
    }

    override fun deleteCategory(categoryId: String): Flow<BasicResponse> =flow{
        emit(categoryRepository.deleteCategory(categoryId))
    }
}