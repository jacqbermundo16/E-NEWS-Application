package codeblock.app.e_news

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import codeblock.app.e_news.databinding.ActivityCheckEmailBinding

class CheckEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDone.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }
}
