package codeblock.app.e_news
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class FullNewsActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_news)

        // Get the URL passed from the previous activity
        val url = intent.getStringExtra("url")

        if (url != null) {
            webView = findViewById(R.id.fullNewsWebView)
            webView.settings.javaScriptEnabled = true

            // Set a WebViewClient to handle page navigation and loading
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // Page has finished loading
                    // You can perform any additional operations here if needed
                }
            }

            // Load the URL into the WebView
            webView.loadUrl(url)
        } else {
            // Handle the case when the URL is null or not provided
            // For example, show an error message or navigate back to the previous activity
            // You can customize this behavior based on your application requirements
        }
    }

    // Optional: Override onBackPressed to handle WebView navigation
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
