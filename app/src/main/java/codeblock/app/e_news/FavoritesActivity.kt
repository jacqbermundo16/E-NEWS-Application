package codeblock.app.e_news

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class FavoritesActivity: AppCompatActivity(), Adapter.OnBookmarkClickListener {
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: Adapter
    private lateinit var favoritesList: MutableList<Articles>

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userFavoritesReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_favorites)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        favoritesList = mutableListOf()
        favoritesRecyclerView = findViewById(R.id.FavNews)
        favoritesAdapter = Adapter(this, favoritesList)

        favoritesRecyclerView.adapter = favoritesAdapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch and display the user's favorite articles
        fetchUserFavorites()

        // Call setFavoritesData() here after initializing the adapter and setting the layout manager
        setFavoritesData(favoritesList)
    }

    private fun fetchUserFavorites() {
        val user = auth.currentUser
        user?.let {
            userFavoritesReference =
                database.reference.child("users").child(it.uid).child("favorites")

            // Declare newFavoritesList before both addValueEventListener calls
            val newFavoritesList = mutableListOf<Articles>()

            userFavoritesReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    newFavoritesList.clear()
                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Articles::class.java)
                        article?.let {
                            newFavoritesList.add(it)

                            // Check if the article exists in the user's favorites and update isFavorite accordingly
                            val index =
                                favoritesList.indexOfFirst { favArticle -> favArticle.id == it.id }
                            if (index != -1) {
                                it.isFavorite = true

                            }
                        }
                    }

                    // Add log statements to see the fetched data and the size of the favoritesList
                    Log.d("FavoritesActivity", "Fetched data: $newFavoritesList")
                    Log.d("FavoritesActivity", "favoritesList size: ${favoritesList.size}")

                    favoritesAdapter.notifyDataSetChanged() // Notify the adapter about dataset changes
                    // Compare the new list with the current list to identify specific changes
                    val diffResult = DiffUtil.calculateDiff(
                        ArticlesDiffUtilCallback(
                            favoritesList,
                            newFavoritesList
                        )
                    )

                    favoritesList.clear()
                    favoritesList.addAll(newFavoritesList)
                    diffResult.dispatchUpdatesTo(favoritesAdapter)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FavoritesActivity", "Database error: ${error.message}")
                }


            })
        }
    }

    private fun setFavoritesData(newFavoritesList: List<Articles>) {
        favoritesList.clear()
        favoritesList.addAll(newFavoritesList)
        favoritesAdapter.notifyDataSetChanged()
    }

    override fun onBookmarkClick(article: Articles) {
        // Update the article's isFavorite property
        article.isFavorite = !article.isFavorite

        // Save the updated article to Firebase Realtime Database
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            val userFavoritesReference = databaseReference.child(it.uid).child("favorites")
            userFavoritesReference.child(article.id.toString()).setValue(article)
        }

        // Update the UI of the heart button (bookmark CheckBox)
        val position = favoritesList.indexOf(article)
        if (position != -1) {
            favoritesAdapter.notifyItemChanged(position)
        }

        // Show a toast message to inform the user about the bookmark action
        if (article.isFavorite) {
            Toast.makeText(this, "Article bookmarked!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Article removed from bookmarks!", Toast.LENGTH_SHORT).show()
        }
    }

}



