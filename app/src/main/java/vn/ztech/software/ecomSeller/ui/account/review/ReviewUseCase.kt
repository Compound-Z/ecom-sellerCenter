package vn.ztech.software.ecomSeller.ui.account.review

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import vn.ztech.software.ecomSeller.model.Review
import vn.ztech.software.ecomSeller.model.ReviewQueue
import vn.ztech.software.ecomSeller.repository.IReviewRepository
import vn.ztech.software.ecomSeller.repository.ReviewRepository


interface IReviewUseCase{
    suspend fun getAllReview(starFilter: Int?): Flow<PagingData<Review>>
    suspend fun getListReviewOfAProduct(productId: String, startFilter: Int?): Flow<PagingData<Review>>
    suspend fun getMyReviewQueue(startFilter: String): Flow<PagingData<ReviewQueue>>
    suspend fun createReview(productId: String, reviewQueueId: String, rating: Int, content: String): Review
    suspend fun updateReview(reviewId: String, rating: Int, content: String): Review
}

class ReviewUseCase( private val reviewRepository: IReviewRepository): IReviewUseCase {
    override suspend fun getAllReview(starFilter: Int?): Flow<PagingData<Review>>
    = reviewRepository.getAllReview(starFilter)
    override suspend fun getListReviewOfAProduct(
        productId: String,
        starFilter: Int?
    ) = reviewRepository.getListReviewOfAProduct(productId, starFilter)

    override suspend fun getMyReviewQueue(starFilter: String): Flow<PagingData<ReviewQueue>>
    = reviewRepository.getMyReviewQueue(starFilter)

    override suspend fun createReview(
        productId: String,
        reviewQueueId: String,
        rating: Int,
        content: String
    ): Review {
        return reviewRepository.createReview(productId,reviewQueueId,rating,content)
    }

    override suspend fun updateReview(reviewId: String, rating: Int, content: String): Review {
        return reviewRepository.updateReview(reviewId,rating,content)
    }

}