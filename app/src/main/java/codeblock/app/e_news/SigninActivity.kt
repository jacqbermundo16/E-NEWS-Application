package codeblock.app.e_news

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import codeblock.app.e_news.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Authentication
        auth = FirebaseAuth.getInstance()

        //Username/Email Validation
        val usernameStream = RxTextView.textChanges(binding.entEmail)
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
            val email = binding.entEmail.text.toString().trim()
            val password = binding.entPass.text.toString().trim()
            signinUser(email, password)
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
            binding.entEmail.error = if (isNotValid) "$text Must be filled." else null
        else if (text == "Password")
            binding.entPass.error = if (isNotValid) "$text Must be filled." else null
    }
    private fun signinUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signin ->
                if (signin.isSuccessful) {
                    Intent(this, HomeActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this, "Login Successfully!", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this, signin.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}