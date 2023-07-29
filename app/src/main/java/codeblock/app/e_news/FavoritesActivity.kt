package codeblock.app.e_news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FavoritesActivity: AppCompatActivity() {
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
            userFavoritesReference = database.reference.child("users").child(it.uid).child("favorites")

            // Declare newFavoritesList before both addValueEventListener calls
            val newFavoritesList = mutableListOf<Articles>()

            userFavoritesReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (articleSnapshot in snapshot.children) {
                        val article = articleSnapshot.getValue(Articles::class.java)
                        article?.let { favoritesList.add(it) }
                    }
                    // Compare the new list with the current list to identify specific changes
                    val diffResult = DiffUtil.calculateDiff(ArticlesDiffUtilCallback(favoritesList, newFavoritesList))

                    favoritesList.clear()
                    favoritesList.addAll(newFavoritesList)
                    diffResult.dispatchUpdatesTo(favoritesAdapter)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during data retrieval
                }


            })
        }
    }

    private fun setFavoritesData(newFavoritesList: List<Articles>) {
        favoritesList.clear()
        favoritesList.addAll(newFavoritesList)
        favoritesAdapter.notifyDataSetChanged()
    }


}
