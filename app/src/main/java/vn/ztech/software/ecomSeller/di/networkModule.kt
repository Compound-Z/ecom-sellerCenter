package vn.ztech.software.ecomSeller.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import vn.ztech.software.ecomSeller.network.ApiGenerator
import vn.ztech.software.ecomSeller.network.IApiGenerator
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.gson.GsonConverterFactory
import vn.ztech.software.ecomSeller.common.Constants
import vn.ztech.software.ecomSeller.network.ApiNetworkInterceptor

val API = named("api")

fun networkModule() = module {

    single<IApiGenerator> { ApiGenerator(get(API)) }

    single<Retrofit.Builder>(API) {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(Constants.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
    }

    single<Gson> { GsonBuilder().create() }


    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(ApiNetworkInterceptor(get(), get()))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}