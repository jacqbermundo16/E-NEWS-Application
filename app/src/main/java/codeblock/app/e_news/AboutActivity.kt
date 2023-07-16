package codeblock.app.e_news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3
import kotlin.math.max

class AboutActivity : AppCompatActivity() {

    private lateinit var viewPager2 : ViewPager2
    private var descList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        postToList()

        viewPager2.adapter = ViewPagerAdapter(descList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager2)

    }

    private fun addToList(description: String) {
        descList.add(description)
    }

    private fun postToList(){
        for (i in 1..3) {
            addToList("ABOUT THE APP")
            addToList("ABOUT THE COMPANY")
            addToList("ABOUT THE DEVELOPERS")
        }
    }
}