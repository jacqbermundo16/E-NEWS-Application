package codeblock.app.e_news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Picasso

class NewsPreview : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvSource: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvDesc: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_preview)

        tvTitle = findViewById(R.id.newsHeading)
        tvSource = findViewById(R.id.newsSource)
        tvTime = findViewById(R.id.newsDate)
        tvDesc = findViewById(R.id.newsContent)
        imageView = findViewById(R.id.newsImage)


        val intent = intent
        val title = intent.getStringExtra("title")
        val source = intent.getStringExtra("source")
        val time = intent.getStringExtra("time")
        val desc = intent.getStringExtra("desc")
        val imageUrl = intent.getStringExtra("imageUrl")
        val url = intent.getStringExtra("url")

        tvTitle.text = title
        tvSource.text = source
        tvTime.text = time
        tvDesc.text = desc

        //Picasso.get().load(imageUrl).into(imageView)



        /*val btnReadNews = findViewById<Button>(R.id.btnReadNews)
        btnReadNews.setOnClickListener {
            val fullNewsIntent = Intent(this, FullNewsActivity::class.java)
            fullNewsIntent.putExtra("url", url)
            startActivity(fullNewsIntent)
        }*/


        val shareFavs = findViewById<ImageView>(R.id.shareFavs)
        // Set OnClickListener for shareFavs ImageView to handle sharing functionality
    }
}