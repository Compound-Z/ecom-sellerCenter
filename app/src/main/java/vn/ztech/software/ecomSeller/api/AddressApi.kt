package vn.ztech.software.ecomSeller.api

import androidx.annotation.Keep
import retrofit2.http.*
import vn.ztech.software.ecomSeller.api.request.AddAddressRequest
import vn.ztech.software.ecomSeller.model.Address
import vn.ztech.software.ecomSeller.model.District
import vn.ztech.software.ecomSeller.model.Province
import vn.ztech.software.ecomSeller.model.Ward

@Keep
interface IAddressApi{
    @GET("/api/v1/addresses")
    suspend fun getAddresses(): Address?
    @GET("/api/v1/addresses/provinces")
    suspend fun getProvinces(): List<Province>
    @GET("/api/v1/addresses/districts/{provinceId}")
    suspend fun getDistricts(@Path("provinceId") provinceId: Int): List<District>
    @GET("/api/v1/addresses/wards/{districtId}")
    suspend fun getWards(@Path("districtId") districtId: Int): List<Ward>
    @POST("/api/v1/addresses")
    suspend fun addAddress(@Body request: AddAddressRequest): Address
    @PATCH("/api/v1/addresses/{addressItemId}")
    suspend fun updateAddress(@Path("addressItemId")addressItemId: String, @Body request: AddAddressRequest): Address
    @DELETE("/api/v1/addresses/{addressItemId}")
    suspend fun deleteAddress(@Path("addressItemId") addressItemId: String): Address
}