import codeblock.app.e_news.models.Headlines
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("everything")
    fun getNewsByQuery(
        @Query("q") query: String?,
        @Query("apiKey") apiKey: String?
    ): Call<Headlines?>?
}
