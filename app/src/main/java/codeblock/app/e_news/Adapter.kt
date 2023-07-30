
package codeblock.app.e_news

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class Adapter(var context: Context, articles: List<Articles>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    var articles: List<Articles>

    init {
        this.articles = articles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_topnews_item, parent, false)
        Log.d("Adapter", "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:  ViewHolder, position: Int) {
        val a: Articles = articles[position]
        Log.d("Adapter", "Article Title: ${a.title}")
        holder.tvHeading.text = a.title
        holder.tvSource.text = a.source?.name ?: "Unknown"
        holder.tvDate.text = a.publishedAt
        val imageUrl: String? = a.urlToImage
        val httpsImageUrl = imageUrl?.replace("http://", "https://")

        Log.d("Adapter", "Image URL: $imageUrl")

        Picasso.with(context)
            .load(httpsImageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .into(holder.imageView, object : Callback {
                override fun onSuccess() {
                    Log.d("Adapter", "Image loaded successfully: $imageUrl")
                }

                override fun onError() {
                    Log.d("Adapter", "Error loading image: $imageUrl")
                }
            })

        // Step 4: Set the click listener on the news article item
        holder.cardView.setOnClickListener {
            val intent = Intent(context, NewsPreview::class.java)
            intent.putExtra("title", a.title)
            intent.putExtra("source", a.source?.name)
            intent.putExtra("time", (a.publishedAt))
            intent.putExtra("desc", a.description)
            intent.putExtra("url", a.url)

            val imageUrl: String? = a.urlToImage
            val httpsImageUrl = imageUrl?.replace("http://", "https://")

            Log.d("Adapter", "Image URL: $imageUrl")

            Picasso.with(context)
                .load(httpsImageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.imageView, object : Callback {
                    override fun onSuccess() {
                        Log.d("Adapter", "Image loaded successfully: $imageUrl")
                    }

                    override fun onError() {
                        Log.d("Adapter", "Error loading image: $imageUrl")
                    }
                })

            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvHeading: TextView
        var tvSource: TextView
        var tvDate: TextView
        var imageView: ImageView
        var cardView: CardView

        init {
            tvHeading = itemView.findViewById<TextView>(R.id.newsHeading)
            tvSource = itemView.findViewById<TextView>(R.id.newsSource)
            tvDate = itemView.findViewById<TextView>(R.id.newsDate)
            imageView = itemView.findViewById(R.id.newsImage)
            cardView = itemView.findViewById<CardView>(R.id.cardView)
        }
    }

    // News Preview

    // Step 1: Add a click listener to the Adapter
    interface OnItemClickListener {
        fun onItemClick(article: Articles)
    }

    // Step 2: Create a member variable to hold the listener
    private var itemClickListener: OnItemClickListener? = null

    // Step 3: Set the click listener from the fragment
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }



}