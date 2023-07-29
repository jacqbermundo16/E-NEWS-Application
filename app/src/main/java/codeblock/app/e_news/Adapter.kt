
package codeblock.app.e_news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class Adapter(var context: Context, articles: List<Articles>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    var articles: List<Articles>
    private var onBookmarkClickListener: OnBookmarkClickListener? = null

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
        val httpsImageUrl = imageUrl?.replace("http://", "https://")
        val article = articles[position]
        holder.bind(article)

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
        private val heartCheckBox: CheckBox = itemView.findViewById(R.id.saveFavs)

        init {
            tvHeading = itemView.findViewById<TextView>(R.id.newsHeading)
            tvSource = itemView.findViewById<TextView>(R.id.newsSource)
            tvDate = itemView.findViewById<TextView>(R.id.newsDate)
            imageView = itemView.findViewById(R.id.newsImage)
            cardView = itemView.findViewById<CardView>(R.id.cardView)

            // Set the click listener for the heart CheckBox
            heartCheckBox.setOnClickListener {
                val article = articles[adapterPosition]
                onBookmarkClick(article)
            }
        }

        fun bind(article: Articles) {
            // Set the heart CheckBox state based on the isFavorite property
            heartCheckBox.isChecked = article.isFavorite
        }

    }

    private fun onBookmarkClick(article: Articles) {
        // Handle the bookmark CheckBox click here
        Log.d("Adapter", "onBookmarkClick: Article Title: ${article.title}, isFavorite: ${article.isFavorite}")
        article.isFavorite = !article.isFavorite
        Log.d("Adapter", "onBookmarkClick: Article Title: ${article.title}, isFavorite: ${article.isFavorite}")

        // Save the updated article to Firebase Realtime Database
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            val userFavoritesReference = databaseReference.child(it.uid).child("favorites")
            userFavoritesReference.child(article.id.toString()).setValue(article)
        }

        // Update the UI of the heart button (bookmark CheckBox)
        val position = articles.indexOf(article)
        if (position != -1) {
            notifyItemChanged(position)
        }

        // Show a toast message to inform the user about the bookmark action
        if (article.isFavorite) {
            Toast.makeText(context, "Article bookmarked!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Article removed from bookmarks!", Toast.LENGTH_SHORT).show()
        }

        // Notify the RecyclerView that the data for the specific item has changed
        notifyItemChanged(articles.indexOf(article))
    }

    interface OnBookmarkClickListener {
        fun onBookmarkClick(article: Articles)
    }

    fun setOnBookmarkClickListener(listener: OnBookmarkClickListener) { onBookmarkClickListener = listener
    }
}
