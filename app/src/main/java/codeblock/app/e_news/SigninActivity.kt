package codeblock.app.e_news

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import codeblock.app.e_news.databinding.ActivitySigninBinding
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class SigninActivity : AppCompatActivity() {

    private lateinit var  binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Username Validation
        val usernameStream = RxTextView.textChanges(binding.enterUName)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

        //Password Validation
        val passStream = RxTextView.textChanges(binding.entPass)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

        //Button enable
        val invalidFieldStream = io.reactivex.Observable.combineLatest(
            usernameStream,
            passStream,
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid }
        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        // Onclick
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }

        binding.noAccTrue.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,LandingActivity::class.java))
        }
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.enterUName.error = if (isNotValid) "$text Must be filled." else null
        else if (text == "Password")
            binding.entPass.error = if (isNotValid) "$text Must be filled." else null
    }
}