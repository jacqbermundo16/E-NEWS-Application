package codeblock.app.e_news.models

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Articles(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @SerializedName("source")
    var source: Source?,

    @SerializedName("author")
    var author: String?,

    @SerializedName("title")
    var title: String?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("urlToImage")
    var urlToImage: String?,

    @SerializedName("publishedAt")
    var publishedAt: String?,

    var isFavorite: Boolean = false
) : Serializable
