package vn.ztech.software.ecomSeller.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.ztech.software.ecomSeller.api.IReviewApi
import vn.ztech.software.ecomSeller.api.request.GetReviewsRequest
import vn.ztech.software.ecomSeller.model.Review
import java.lang.Exception

class ReviewPagingSource (val request: GetReviewsRequest, private val reviewApi: IReviewApi): PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            Log.d("xxx","***")
            val position = params.key ?: 1
            request.page = position
            val response = reviewApi.getAllReview(request)
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

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        val key = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        Log.d("xxxRefreshKey", key.toString())
        return key
    }

}