package vn.ztech.software.ecomSeller.repository

import vn.ztech.software.ecomSeller.api.IShippingApi
import vn.ztech.software.ecomSeller.api.request.GetShippingOptionsReq
import vn.ztech.software.ecomSeller.api.response.ShippingOption

interface IShippingRepository{
    suspend fun getShippingOptions(shippingOptionsReq: GetShippingOptionsReq): List<ShippingOption>
   
}

class ShippingRepository(private val shippingApi: IShippingApi): IShippingRepository{
    override suspend fun getShippingOptions(shippingOptionsReq: GetShippingOptionsReq): List<ShippingOption> {
        return shippingApi.getShippingOptions(shippingOptionsReq)
    }
}