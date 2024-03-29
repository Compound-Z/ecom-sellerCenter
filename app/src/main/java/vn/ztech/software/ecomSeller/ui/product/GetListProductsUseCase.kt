package vn.ztech.software.ecomSeller.ui.product

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.request.QuickUpdateProductRequest
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.repository.IProductRepository
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.util.CustomError
import java.io.File

interface IListProductUseCase{
    suspend fun getListProducts(): Flow<PagingData<Product>>
    suspend fun search(searchWords: String): Flow<PagingData<Product>>
    suspend fun getOrigins(): Flow<List<Country>>
    suspend fun uploadImage(file: File): Flow<UploadImageResponse>
    suspend fun createProduct(createProductRequest: CreateProductRequest?): Flow<Product>
    suspend fun updateProduct(productId: String, createProductRequest: CreateProductRequest?): Flow<Product>
    suspend fun quickUpdateProduct(productId: String, request: QuickUpdateProductRequest): Flow<Product>
    suspend fun deleteProduct(productId: String): Flow<BasicResponse>
    suspend fun getOneProduct(productId: String): Flow<Product>

}

class ListProductsUseCase(private val productRepository: IProductRepository): IListProductUseCase {
    override suspend fun getListProducts(): Flow<PagingData<Product>>{
        return productRepository.getListProducts()
    }

    override suspend fun search(searchWords: String): Flow<PagingData<Product>> {
        return productRepository.search(searchWords)
    }

    override suspend fun getOrigins(): Flow<List<Country>> = flow {
        emit(productRepository.getOrigins())
    }
    override suspend fun uploadImage(file: File): Flow<UploadImageResponse> = flow{
        if(!"jpg|png|jpeg".contains(file.extension)) throw CustomError(customMessage = "Wrong file type, please submit image")
        if (file.length()/1024>Constants.MAX_FILE_SIZE) throw CustomError(customMessage = "File is too large, please submit file smaller than 4M")
        else emit(productRepository.uploadImage(file))
    }

    override suspend fun createProduct(createProductRequest: CreateProductRequest?): Flow<Product> = flow {
        val product = productRepository.createProduct(createProductRequest)
        emit(product)
    }
    override suspend fun updateProduct(productId: String, createProductRequest: CreateProductRequest?): Flow<Product> = flow {
        val product = productRepository.updateProduct(productId, createProductRequest)
        emit(product)
    }

    override suspend fun quickUpdateProduct(
        productId: String,
        request: QuickUpdateProductRequest
    ): Flow<Product> = flow{
        val product = productRepository.quickUpdateProduct(productId, request)
        emit(product)
    }

    override suspend fun deleteProduct(productId: String): Flow<BasicResponse> = flow {
        val product = productRepository.deleteProduct(productId)
        emit(product)
    }

    override suspend fun getOneProduct(productId: String): Flow<Product> = flow {
        emit(productRepository.getOneProduct(productId))
    }

}