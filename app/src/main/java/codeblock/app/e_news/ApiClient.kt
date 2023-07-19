

package codeblock.app.e_news

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor() {
    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface
        get() = retrofit.create(ApiInterface::class.java)

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"
        private var apiClient: ApiClient? = null

        @Synchronized
        fun getInstance(): ApiClient {
            if (apiClient == null) {
                apiClient = ApiClient()
            }
            return apiClient as ApiClient
        }
    }
}
