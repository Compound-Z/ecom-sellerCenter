package vn.ztech.software.ecomSeller.ui.category

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.repository.ICategoryRepository
import vn.ztech.software.ecomSeller.util.CustomError
import java.io.File

interface IListCategoriesUseCase{
    suspend fun getListCategories(): Flow<List<Category>>
    suspend fun getListProductsInCategory(category: String): Flow<List<Product>>
    suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>>
    fun uploadImage(file: File): Flow<UploadImageResponse>
    fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category>
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

    override fun uploadImage(file: File): Flow<UploadImageResponse> = flow{
        if(!"jpg|png|jpeg".contains(file.extension)) throw CustomError(customMessage = "Wrong file type, please submit image")
        if (file.length()/1024>2048) throw CustomError(customMessage = "File is too large, please submit file smaller than 2M")
        else emit(categoryRepository.uploadImage(file))
    }

    override fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category> = flow {
        emit(categoryRepository.createCategory(createCategoryRequest))
    }
}