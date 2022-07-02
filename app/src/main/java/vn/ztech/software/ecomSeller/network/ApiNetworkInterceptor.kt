package vn.ztech.software.ecomSeller.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.Response
import vn.ztech.software.ecomSeller.api.response.TokenResponse
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.database.local.user.UserManager
import vn.ztech.software.ecomSeller.exception.RefreshTokenExpiredException
import vn.ztech.software.ecomSeller.exception.ResourceException
import vn.ztech.software.ecomSeller.exception.UnauthenticatedException
import vn.ztech.software.ecomSeller.util.CustomError
import java.net.HttpURLConnection
import kotlin.coroutines.CoroutineContext

class ApiNetworkInterceptor(private val gson: Gson, private val userManager: UserManager): Interceptor, CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job

    companion object {
        val HEADER_AUTHORIZATION = "Authorization"
    }

    val TAG = "ApiNetworkInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
        var token: String = ""
        runBlocking {
            token = userManager.getAccessToken()
            delay(500)
        }
        request.addHeader(HEADER_AUTHORIZATION, "Bearer $token")


        var response = chain.proceed(request.build())
        Log.d("RESPONSE", response.peekBody(2048).string() +" " + response?.code)

        //if there is a error, throw errors
        if(response.code < HttpURLConnection.HTTP_OK || response.code >= HttpURLConnection.HTTP_BAD_REQUEST){
            val responseObj = response.toObject<ApiErrorMessageModel>()
            when(response.code) {
                HttpURLConnection.HTTP_BAD_REQUEST -> {
                    throw ResourceException(responseObj?.message?:"Bad request")
                }
                HttpURLConnection.HTTP_NOT_FOUND -> {
                    throw ResourceException(responseObj?.message?:"Not found")
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    if (responseObj?.message?.startsWith("Giaohangnhanh") == true){
                        throw ResourceException(responseObj.message.toString())
                    }
                    throw ResourceException("System error")
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    when(responseObj?.message){
                        "Invalid Credentials" -> throw UnauthenticatedException()
                        "Invalid refresh token" -> throw RefreshTokenExpiredException(responseObj.message)
                        "Invalid access token" -> {
                                val tokenRefreshResponse = requestRefreshToken(chain)
                                /**check response of new access token*/
                                if (tokenRefreshResponse.code == 401 || tokenRefreshResponse.code == 403) {
                                    throw RefreshTokenExpiredException("Invalid refresh token")
                                } else if (tokenRefreshResponse.code == 200) {

                                    val authResponseObj = tokenRefreshResponse.toObject<TokenResponse>()
                                    /**save the new token and refresh token in preference and continue with the earlier request*/
                                    runBlocking {
                                        userManager.saveLogs(accessToken = authResponseObj?.accessToken)
                                    }
                                    /**close this response before return a new one, this is a must, otherwise get an error*/
                                    tokenRefreshResponse.close()
                                    return chain.proceed(
                                                request
                                                .header(HEADER_AUTHORIZATION, "Bearer ${authResponseObj?.accessToken?.token}")
                                                .build()
                                    )
                                }
                        }
                    }
                }
                HttpURLConnection.HTTP_UNAVAILABLE -> {
                    if(responseObj?.message == "Verify OTP failed!"
                        || responseObj?.message == "Can not send OTP code"){
                        throw ResourceException(responseObj?.message.toString())
                    }else{
                        throw ResourceException("Service unavailable")
                    }
                }
            }
        }

        return response
    }

    private fun requestRefreshToken(chain: Interceptor.Chain): Response {
        /**get refresh token*/
        var refreshToken = ""
        runBlocking {
            refreshToken = userManager.getRefreshToken()
            delay(500)
        }
        /**build new request to get new access token*/
        val contentType = "application/json".toMediaType()
        val body = RequestBody.create(contentType, "{\"refreshToken\":\"${refreshToken}\"}")
        val authRequest = chain.request().newBuilder()
            .url(Constants.getBaseUrl()+"/api/v1/auth/refresh-token")
            .post(body)
            .build()
        return chain.proceed(authRequest)
    }
    private inline fun <reified T> Response.toObject(): T? {
        var authResponseObj: T? = null
        this.body?.let {
            try {
                authResponseObj = gson.fromJson(
                    it.string(),
                    T::class.java
                )
                Log.d("RESPONSE OBJ", authResponseObj.toString())
            } catch (e: Exception) {
                throw CustomError(customMessage = "Response parsing")
            }
        }

        if(authResponseObj==null) throw CustomError(customMessage = "Response parsing")
        else return authResponseObj
    }
}