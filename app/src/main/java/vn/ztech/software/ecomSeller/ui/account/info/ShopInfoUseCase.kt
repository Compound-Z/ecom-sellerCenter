package vn.ztech.software.ecomSeller.ui.account.info

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.model.Shop
import vn.ztech.software.ecomSeller.repository.IShopRepository

interface IShopInfoUseCase{
    suspend fun getShopInfo(): Flow<Shop>

}

class ShopInfoUseCase(private val shopRepository: IShopRepository): IShopInfoUseCase {
    override suspend fun getShopInfo(): Flow<Shop> = flow {
        emit(shopRepository.getShopInfo())
    }
}
 