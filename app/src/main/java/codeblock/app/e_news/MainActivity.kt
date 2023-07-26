package codeblock.app.e_news

import News
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import codeblock.app.e_news.databinding.ActivityMainBinding
import codeblock.app.e_news.models.Articles
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val articles: ArrayList<Articles> = ArrayList()

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sideNavigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
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

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize the AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && user.isEmailVerified) {
            } else {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
        }

        // Add the AuthStateListener
        auth.addAuthStateListener(authStateListener)

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
            R.id.logout -> logout()
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

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun logout() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            auth.signOut()
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
