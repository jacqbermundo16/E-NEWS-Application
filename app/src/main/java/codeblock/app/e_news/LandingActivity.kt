package codeblock.app.e_news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val loginButton = findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            val Intent = Intent(this,SigninActivity::class.java)
            startActivity(Intent)
        }

        val signupButton = findViewById<Button>(R.id.btn_signup)
        signupButton.setOnClickListener {
            val Intent = Intent(this,SignupActivity::class.java)
            startActivity(Intent)
        }

        val aboutButton = findViewById<Button>(R.id.btn_about)
        aboutButton.setOnClickListener {
            val Intent = Intent(this,AboutActivity::class.java)
            startActivity(Intent)
        }
    }
}