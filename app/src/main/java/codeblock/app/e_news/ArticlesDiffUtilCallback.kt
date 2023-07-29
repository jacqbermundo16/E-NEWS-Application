package codeblock.app.e_news

import androidx.recyclerview.widget.DiffUtil
import codeblock.app.e_news.models.Articles

class ArticlesDiffUtilCallback(private val oldList: List<Articles>, private val newList: List<Articles>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
