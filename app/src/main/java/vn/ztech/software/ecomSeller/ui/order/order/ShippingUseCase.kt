package vn.ztech.software.ecomSeller.ui.order.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import vn.ztech.software.ecomSeller.api.request.GetShippingOptionsReq
import vn.ztech.software.ecomSeller.api.response.ShippingOption
import vn.ztech.software.ecomSeller.repository.IShippingRepository

interface IShippingUserCase{
    suspend fun getShippingOptions(getShippingOptionsReq: GetShippingOptionsReq): Flow<List<ShippingOption>>
}

class ShippingUseCase(private val shippingRepository: IShippingRepository): IShippingUserCase {

    override suspend fun getShippingOptions(getShippingOptionsReq: GetShippingOptionsReq): Flow<List<ShippingOption>> = flow {
        val shippingOptions = shippingRepository.getShippingOptions(getShippingOptionsReq).filter { it.name.isNotBlank() }.sortedBy { it.fee.total }
        emit(shippingOptions)
    }
}