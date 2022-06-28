package vn.ztech.software.ecomSeller.repository

import vn.ztech.software.ecomSeller.api.IProductApi
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.ProductDetails

interface IProductRepository {
    suspend fun getListProducts(): List<Product>
    suspend fun getProductDetails(productId: String): ProductDetails
    suspend fun search(searchWords: String): List<Product>
}

class ProductRepository(private val productApi: IProductApi): IProductRepository{
    override suspend fun getListProducts(): List<Product> {
        return productApi.getListProducts()
    }

    override suspend fun getProductDetails(productId: String): ProductDetails {
        return productApi.getProductDetails(productId)
    }

    override suspend fun search(searchWords: String): List<Product> {
        return productApi.search(searchWords)
    }

}