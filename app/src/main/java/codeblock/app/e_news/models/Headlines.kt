package codeblock.app.e_news.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Headlines {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("totalResults")
    @Expose
    var totalResults: String? = null

    @SerializedName("articles")
    @Expose
    var articles: List<Articles>? = null
}