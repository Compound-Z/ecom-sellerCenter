package vn.ztech.software.ecomSeller.repository

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
            val position = params.key ?: 1
            request.page = position
            val response = orderApi.getOrders(request)
            LoadResult.Page(data = response.docs,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if(response.docs.isEmpty()) null else position + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }
    //todo: anchorPos, null next

    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}