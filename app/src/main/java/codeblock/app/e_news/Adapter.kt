
package codeblock.app.e_news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import com.squareup.picasso.Picasso

class Adapter(var context: Context, articles: List<Articles>) :
    RecyclerView.Adapter<Adapter.ViewHolder?>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a: Articles = articles[position]
        Log.d("Adapter", "Article Title: ${a.title}")
        holder.tvHeading.text = a.title
        holder.tvSource.text = a.source?.name ?: "Unknown"
        holder.tvDate.text = a.publishedAt
        val imageUrl: String? = a.urlToImage
        Picasso.with(context).load(imageUrl).into(holder.imageView)
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
}