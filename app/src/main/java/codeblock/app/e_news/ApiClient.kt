package codeblock.app.e_news

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor() {
    init {
        retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val api: ApiInterface
        get() = retrofit.create(ApiInterface::class.java)

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=c63f2d130d9a4ca2a0610a2c1682033a"
        private var apiClient: ApiClient? = null
        private lateinit var retrofit: Retrofit

        @get:Synchronized
        val instance: ApiClient?
            get() {
                if (apiClient == null) {
                    apiClient = ApiClient()
                }
                return apiClient
            }
    }
}