package vn.ztech.software.ecomSeller.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import vn.ztech.software.ecomSeller.api.IProductApi
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.api.response.UploadImageResponse
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.model.ProductDetails
import java.io.File

interface IProductRepository {
    suspend fun getListProducts(): List<Product>
    suspend fun getProductDetails(productId: String): ProductDetails
    suspend fun search(searchWords: String): List<Product>
    suspend fun getOrigins(): List<Country>
    suspend fun uploadImage(file: File): UploadImageResponse
    suspend fun createProduct(createProductRequest: CreateProductRequest?): Product

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

    override suspend fun getOrigins(): List<Country> {
        return productApi.getOrigins()
    }
    override suspend fun uploadImage(file: File): UploadImageResponse {
        val requestFile: RequestBody = file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image",file.name, requestFile)
        return productApi.uploadImage(body)
    }

    override suspend fun createProduct(createProductRequest: CreateProductRequest?): Product {
        return productApi.createProduct(createProductRequest)
    }

}