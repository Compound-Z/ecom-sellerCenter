package vn.ztech.software.ecomSeller.ui.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.repository.IProductRepository
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.util.CustomError
import java.io.File

interface IListProductUseCase{
    suspend fun getListProducts(): Flow<List<Product>>
    suspend fun search(searchWords: String): Flow<List<Product>>
    suspend fun getOrigins(): Flow<List<Country>>
    suspend fun uploadImage(file: File): Flow<UploadImageResponse>
    suspend fun createProduct(createProductRequest: CreateProductRequest?): Flow<Product>

}

class ListProductsUseCase(private val productRepository: IProductRepository): IListProductUseCase {
    override suspend fun getListProducts(): Flow<List<Product>> = flow{
        val listProducts = productRepository.getListProducts()
        emit(listProducts)
    }

    override suspend fun search(searchWords: String): Flow<List<Product>> = flow{
        emit(productRepository.search(searchWords))
    }

    override suspend fun getOrigins(): Flow<List<Country>> = flow {
        emit(productRepository.getOrigins())
    }
    override suspend fun uploadImage(file: File): Flow<UploadImageResponse> = flow{
        if(!"jpg|png|jpeg".contains(file.extension)) throw CustomError(customMessage = "Wrong file type, please submit image")
        if (file.length()/1024>2048) throw CustomError(customMessage = "File is too large, please submit file smaller than 2M")
        else emit(productRepository.uploadImage(file))
    }

    override suspend fun createProduct(createProductRequest: CreateProductRequest?): Flow<Product> = flow {
        val product = productRepository.createProduct(createProductRequest)
        emit(product)
    }

}