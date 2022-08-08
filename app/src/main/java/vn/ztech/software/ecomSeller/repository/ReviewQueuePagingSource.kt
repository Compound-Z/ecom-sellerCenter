package vn.ztech.software.ecomSeller.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.ztech.software.ecomSeller.api.IReviewApi
import vn.ztech.software.ecomSeller.api.request.GetMyReviewQueueRequest
import vn.ztech.software.ecomSeller.api.request.GetReviewsRequest
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.model.ReviewQueue
import java.lang.Exception

class ReviewQueuePagingSource (val request: GetMyReviewQueueRequest, private val reviewApi: IReviewApi): PagingSource<Int, ReviewQueue>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewQueue> {
        return try {
            Log.d("xxx","***")
            val position = params.key ?: 1
            request.page = position
            val response = reviewApi.getMyReviewQueue(request)
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

    override fun getRefreshKey(state: PagingState<Int, ReviewQueue>): Int? {
        val key = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        Log.d("xxxRefreshKey", key.toString())
        return key
    }

}