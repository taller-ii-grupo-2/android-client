package com.fiuba.hypechat_app

import android.content.Context
import com.fiuba.hypechat_app.models.Moi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


object RetrofitClient {
    //Logger
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    class CookiesInterceptor : Interceptor {


        var cookie: String? = null

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            cookie?.let { requestBuilder.addHeader("Cookie", it) }

            val response = chain.proceed(requestBuilder.build())
            response.headers()
                .toMultimap()["Set-Cookie"]
                //?.filter { !it.contains("HttpOnly") }
                ?.getOrNull(0)
                ?.also {
                    cookie = it
                }

            return response
        }

        fun clearCookie() {
            cookie = null
        }
    }


    val cookiesInterceptor: CookiesInterceptor by lazy {
        CookiesInterceptor()
    }

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(cookiesInterceptor)
        .addInterceptor(logger)


    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Moi.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

        retrofit.create(ApiService::class.java)
    }

}