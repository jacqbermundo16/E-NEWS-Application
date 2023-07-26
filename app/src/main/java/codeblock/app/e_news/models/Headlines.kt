package codeblock.app.e_news.models

import com.google.gson.annotations.SerializedName

data class Headlines(
    @SerializedName("articles")
    var articles: MutableList<Articles>,

    @SerializedName("status")
    var status: String,

    @SerializedName("totalResults")
    var totalResults: Int? = null
)