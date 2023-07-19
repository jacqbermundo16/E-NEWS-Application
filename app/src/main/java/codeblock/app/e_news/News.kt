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
import codeblock.app.e_news.Models.Headlines
import codeblock.app.e_news.Models.Articles
import codeblock.app.e_news.databinding.FragmentNewsBinding
import codeblock.app.e_news.Adapter


class News : Fragment() {

    private val apiKey = "e047631ef2msh77fa3a93a675193p17e232jsn5aa342e77daf"
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
        val call: Call<Headlines?>? = ApiClient.getInstance().api.getHeadlines(country, "e047631ef2msh77fa3a93a675193p17e232jsn5aa342e77daf")
        call?.enqueue(object : Callback<Headlines?> {
            override fun onResponse(call: Call<Headlines?>, response: Response<Headlines?>) {
                if (response.isSuccessful && response.body()?.articles != null) {
                    val articleList = response.body()?.articles
                    Log.d("Jetty Boy", "API Response Articles size: ${articleList?.size}")
                    articles.clear()
                    articles.addAll(response.body()?.articles!!)
                    adapter.notifyDataSetChanged()
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

    private fun setUpRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.topNews)
        val adapter = Adapter(requireContext(), articles) // Replace 'articles' with your actual list of articles
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        Log.d("NewsFragment", "RecyclerView and Adapter set up")
    }
}

