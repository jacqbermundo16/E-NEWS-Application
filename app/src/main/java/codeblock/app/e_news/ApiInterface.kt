package codeblock.app.e_news

import codeblock.app.e_news.Models.Headlines
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("top-headlines")
    fun getHeadlines(
        @Query("country") country: String?,
        @Query("apiKey") apiKey: String?
    ): Call<Headlines?>?
}