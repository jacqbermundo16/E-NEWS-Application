package codeblock.app.e_news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
        val searchText = view.findViewById<EditText>(R.id.searchText)
        val searchBtn = view.findViewById<Button>(R.id.searchBtn)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter and set it to the RecyclerView
        adapter = Adapter(requireContext(), articles)
        recyclerView.adapter = adapter

        // Fetch initial data on fragment launch
        retrieveJson(apiKey)

        // Set click listener for the search button
        searchBtn.setOnClickListener {
            val searchQuery = searchText.text.toString()
            if (searchQuery.isNotBlank()) {
                searchArticles(searchQuery)
            } else {
                // If the search query is empty, show a toast message
                Toast.makeText(requireContext(), "Enter a search query", Toast.LENGTH_SHORT).show()
            }
        }
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

                    articles.clear()
                    articleList?.let {
                        articles.addAll(it)
                    }
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

    private fun searchArticles(query: String) {
        Log.d("News", "searchArticles function called with query: $query")
        val call: Call<Headlines?>? = ApiClient.getInstance().api.getNewsByQuery(query, apiKey)
        call?.enqueue(object : Callback<Headlines?> {
            override fun onResponse(call: Call<Headlines?>, response: Response<Headlines?>) {
                if (response.isSuccessful && response.body()?.articles != null) {
                    val articleList = response.body()?.articles
                    Log.d("Jetty Boy", "API Response Articles size: ${articleList?.size}")

                    articles.clear()
                    articleList?.let {
                        articles.addAll(it)
                    }
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
