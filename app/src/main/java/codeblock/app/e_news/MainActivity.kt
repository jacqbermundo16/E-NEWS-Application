package codeblock.app.e_news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import codeblock.app.e_news.Models.Articles
import codeblock.app.e_news.Models.Headlines
import codeblock.app.e_news.Models.Source
import codeblock.app.e_news.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val articles: ArrayList<Articles> = ArrayList()

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sideNavigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        recyclerView = findViewById(R.id.topNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter and set it to the RecyclerView
        adapter = Adapter(this, articles)
        recyclerView.adapter = adapter

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostFragment.navController

        sideNavigationView = binding.navSidebarView
        bottomNavigationView = binding.navbar

        setupSideNavigationView(sideNavigationView)
        setupBottomNavigationView(bottomNavigationView)

        drawerLayout = binding.sideNavbar

        val toolbar = binding.sideToolbar
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(News())
            sideNavigationView.setCheckedItem(R.id.news)
        }
    }

    private fun setupSideNavigationView(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupBottomNavigationView(navigationView: BottomNavigationView) {
        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.news -> replaceFragment(News())
                R.id.donate -> replaceFragment(Donate())
                R.id.favorites -> replaceFragment(Favorites())
                R.id.profile -> replaceFragment(Profile())
            }
            true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notifs -> replaceFragment(Notifications())
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
