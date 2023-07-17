package codeblock.app.e_news

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codeblock.app.e_news.models.Articles
import codeblock.app.e_news.models.Headlines
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private val API_KEY = "e047631ef2msh77fa3a93a675193p17e232jsn5aa342e77daf"
    private lateinit var adapter: Adapter
    private val articles: ArrayList<Articles> = ArrayList()


    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.topNews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val country = getCountry()
        retrieveJson(country,API_KEY)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navbar)
        setupWithNavController(bottomNavigationView, navController)

        drawerLayout = findViewById(R.id.side_navbar)

        val toolbar = findViewById<Toolbar>(R.id.side_toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_sidebarView)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(News())
            navigationView.setCheckedItem(R.id.news)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.news -> replaceFragment(News())
            R.id.donate -> replaceFragment(Donate())
            R.id.favorites -> replaceFragment(Favorites())
            R.id.profile -> replaceFragment(Profile())
            R.id.aboutUs -> replaceFragment(About())
            R.id.logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun retrieveJson(country: String, apiKey: String) {
        val call: Call<Headlines?>? = ApiClient.instance?.api?.getHeadlines(country, apiKey)
        call?.enqueue(object : Callback<Headlines?> {
            override fun onResponse(call: Call<Headlines?>, response: Response<Headlines?>) {
                if (response.isSuccessful && response.body()?.articles != null) {
                    articles.clear()
                    articles.addAll(response.body()?.articles!!)
                    adapter = Adapter(this@MainActivity, articles)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Headlines?>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getCountry(): String {
        val locale: Locale = Locale.getDefault()
        return locale.country.toLowerCase(Locale.ROOT)
    }
}