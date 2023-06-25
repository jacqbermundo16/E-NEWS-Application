package codeblock.app.e_news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            val Intent = Intent(this,LandingActivity::class.java)
            startActivity(Intent)
        }
    }
}