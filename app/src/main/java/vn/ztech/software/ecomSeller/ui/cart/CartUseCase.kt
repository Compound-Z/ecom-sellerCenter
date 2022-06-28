package vn.ztech.software.ecomSeller.ui.cart

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.response.BasicResponse
import vn.ztech.software.ecomSeller.api.response.CartProductResponse
import vn.ztech.software.ecomSeller.repository.ICartRepository

interface ICartUseCase{
    suspend fun getListProductsInCart(): Flow<List<CartProductResponse>>
    suspend fun addProductToCart(productId: String): Flow<BasicResponse>
    suspend fun adjustQuantityOfProductInCart(productId: String, quantity: Int): Flow<BasicResponse>
    suspend fun deleteProductFromCart(productId: String): Flow<BasicResponse>
}

class CartUseCase(private val cartRepository: ICartRepository): ICartUseCase{
    override suspend fun getListProductsInCart(): Flow<List<CartProductResponse>> = flow{
        val listProducts = cartRepository.getListProductsInCart()
        emit(listProducts)
    }

    override suspend fun addProductToCart(productId: String): Flow<BasicResponse> = flow {
        emit(cartRepository.addProductToCart(productId))
    }

    override suspend fun adjustQuantityOfProductInCart(
        productId: String,
        quantity: Int
    ): Flow<BasicResponse> = flow {
        emit(cartRepository.adjustQuantityOfProductInCart(productId, quantity))
    }

    override suspend fun deleteProductFromCart(productId: String): Flow<BasicResponse> = flow {
        emit(cartRepository.deleteProductFromCart(productId))
    }

}