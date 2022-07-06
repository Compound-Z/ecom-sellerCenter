package vn.ztech.software.ecomSeller.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.FtsOptions
import vn.ztech.software.ecomSeller.api.IOrderApi
import vn.ztech.software.ecomSeller.api.response.GetOrdersRequest
import vn.ztech.software.ecomSeller.model.Order
import java.lang.Exception

class OrderPagingSource(val request: GetOrdersRequest, private val orderApi: IOrderApi): PagingSource<Int, Order>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
        return try {
            Log.d("xxx","***")
            val position = params.key ?: 1
            request.page = position
            val response = orderApi.getOrders(request)
            val pv = response.prevPage
            val nk = response.nextPage
            Log.d("xxxpv", pv.toString())
            Log.d("xxxpos", position.toString())
            Log.d("xxxnk", nk.toString())

            LoadResult.Page(data = response.docs,
                prevKey = response.prevPage,
                nextKey = response.nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }
    //todo: anchorPos, null next.
    //todo: fix bug: keep position ussing anchor
    //todo: fix bug: reload page is cut

    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        val key = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        Log.d("xxxRefreshKey", key.toString())
        return key
    }

}