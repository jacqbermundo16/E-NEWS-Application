package codeblock.app.e_news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import codeblock.app.e_news.models.Headlines
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class News : Fragment() {

    private val apiKey = "8446b62223ba45538b0c8bea1612f8f4"
    private lateinit var adapter: Adapter
    private val articles: ArrayList<Articles> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.topNews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter and set it to the RecyclerView
        adapter = Adapter(requireContext(), articles)
        recyclerView.adapter = adapter

        retrieveJson(apiKey)
    }

    private fun retrieveJson(apiKey: String) {
        val query1 = "environmental"
        val query2 = "climate"
        val query3 = "nature"
        val query4 = "ecosystem"
        val query5 = "agriculture"

        val query = "$query1 AND $query2 AND $query3 AND $query4 AND $query5"

        Log.d("News", "retrieveJson function called")
        val call: Call<Headlines?>? = ApiClient.getInstance().api.getNewsByQuery(query, apiKey)
        call?.enqueue(object : Callback<Headlines?> {
            override fun onResponse(call: Call<Headlines?>, response: Response<Headlines?>) {
                if (response.isSuccessful && response.body()?.articles != null) {
                    val articleList = response.body()?.articles
                    Log.d("Jetty Boy", "API Response Articles size: ${articleList?.size}")

                    // Log article titles and image URLs
                    for (article in articleList!!) {
                        Log.d("Articles", "Title: ${article.title}, Image URL: ${article.urlToImage}")
                    }

                    articles.clear()
                    articles.addAll(articleList)
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d("Jetty Boy", "API call was not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Headlines?>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
