package vn.ztech.software.ecomSeller.ui.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.repository.IProductRepository
import vn.ztech.software.ecomSeller.model.Product

interface IListProductUseCase{
    suspend fun getListProducts(): Flow<List<Product>>
    suspend fun search(searchWords: String): Flow<List<Product>>
}

class ListProductsUseCase(private val productRepository: IProductRepository): IListProductUseCase {
    override suspend fun getListProducts(): Flow<List<Product>> = flow{
        val listProducts = productRepository.getListProducts()
        emit(listProducts)
    }

    override suspend fun search(searchWords: String): Flow<List<Product>> = flow{
        emit(productRepository.search(searchWords))
    }

}