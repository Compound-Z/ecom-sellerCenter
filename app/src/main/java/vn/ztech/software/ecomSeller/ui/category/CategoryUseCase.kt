package vn.ztech.software.ecomSeller.ui.category

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UpdateCategoryResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.repository.ICategoryRepository
import vn.ztech.software.ecomSeller.util.CustomError
import vn.ztech.software.ecomSeller.util.extension.removeUnderline
import java.io.File

interface IListCategoriesUseCase{
    suspend fun getListCategories(): Flow<List<Category>>
    suspend fun getListProductsInCategory(category: String): Flow<PagingData<Product>>
    suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>>
    suspend fun uploadImage(file: File): Flow<UploadImageResponse>
    suspend fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category>
    suspend fun deleteCategory(categoryId: String): Flow<BasicResponse>
    suspend fun updateCategory(categoryId: String, createCategoryRequest: CreateCategoryRequest): Flow<UpdateCategoryResponse>

}

class ListCategoriesUseCase(private val categoryRepository: ICategoryRepository): IListCategoriesUseCase{
    override suspend fun getListCategories(): Flow<List<Category>> = flow{
        val listCategories = categoryRepository.getListCategories()
        listCategories.forEach() {
            it.name = it.name.removeUnderline()
        }
        emit(listCategories)
    }
    override suspend fun getListProductsInCategory(category: String): Flow<PagingData<Product>> {
        return categoryRepository.getListProductsInCategory(category)
    }
    override suspend fun search(searchWordsCategory: String, searchWordsProduct: String): Flow<List<Product>> = flow{
        emit(categoryRepository.search(searchWordsCategory, searchWordsProduct))
    }
    override suspend fun uploadImage(file: File): Flow<UploadImageResponse> = flow{
        if(!"jpg|png|jpeg".contains(file.extension)) throw CustomError(customMessage = "Wrong file type, please submit image")
        if (file.length()/1024>2048) throw CustomError(customMessage = "File is too large, please submit file smaller than 2M")
        else emit(categoryRepository.uploadImage(file))
    }

    override suspend fun crateCategory(createCategoryRequest: CreateCategoryRequest): Flow<Category> = flow {
        val category = categoryRepository.createCategory(createCategoryRequest)
        category.name = category.name.removeUnderline()
        emit(category)
    }

    override suspend fun deleteCategory(categoryId: String): Flow<BasicResponse> =flow{
        emit(categoryRepository.deleteCategory(categoryId))
    }
    override suspend fun updateCategory(categoryId: String, createCategoryRequest: CreateCategoryRequest): Flow<UpdateCategoryResponse> = flow {
        emit(categoryRepository.updateCategory(categoryId, createCategoryRequest))

    }
}