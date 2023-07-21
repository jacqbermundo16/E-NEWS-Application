package codeblock.app.e_news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import codeblock.app.e_news.models.Headlines
import codeblock.app.e_news.models.Articles


class News : Fragment() {

    private val apiKey = "8446b62223ba45538b0c8bea1612f8f4"
    private lateinit var adapter: codeblock.app.e_news.Adapter
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

        Log.d("NewsFragment", "RecyclerView: $recyclerView")

        // Initialize the adapter and set it to the RecyclerView
        adapter = Adapter(requireContext(), articles)
        recyclerView.adapter = adapter

        val country = getCountry()
        retrieveJson(country, apiKey)
    }

    private fun retrieveJson(country: String, apiKey: String) {
        Log.d("Jetty Boy", "retrieveJson function called")
        val call: Call<Headlines?>? = ApiClient.getInstance().api.getHeadlines("ph", apiKey)
        call?.enqueue(object : Callback<Headlines?> {
            override fun onResponse(call: Call<Headlines?>, response: Response<Headlines?>) {
                if (response.isSuccessful && response.body()?.articles != null) {
                    val articleList = response.body()?.articles
                    Log.d("Jetty Boy", "API Response Articles size: ${articleList?.size}")
                    articles.clear()
                    articles.addAll(articleList!!)
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

    private fun getCountry(): String {
        val locale: Locale = Locale.getDefault()
        return locale.country.lowercase(Locale.ROOT)
    }

}

