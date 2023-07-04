package codeblock.app.e_news

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.core.content.ContextCompat
import codeblock.app.e_news.databinding.ActivitySignupBinding
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Username Validation
        val usernameStream = RxTextView.textChanges(binding.enterUName)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

        //Email Validation
        val emailStream = RxTextView.textChanges(binding.entEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }

        //Password Validation
        val passStream = RxTextView.textChanges(binding.entPass)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

        //Confirm Password Validation
        val conPassStream = io.reactivex.Observable.merge(
            RxTextView.textChanges(binding.entPass)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.conPass.text.toString()
                },
            RxTextView.textChanges(binding.conPass)
                .skipInitialValue()
                .map { conPassword ->
                    conPassword.toString() != binding.entPass.text.toString()
                })
        conPassStream.subscribe {
            showPasswordConfirmAlert(it)
        }

        //Button enable
            val invalidFieldStream = io.reactivex.Observable.combineLatest(
                usernameStream,
                emailStream,
                passStream,
                conPassStream
            ) { usernameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean, confirmPasswordInvalid: Boolean ->
                !usernameInvalid && !emailInvalid && !passwordInvalid && !confirmPasswordInvalid
            }
            invalidFieldStream.subscribe { isValid ->
                if (isValid) {
                    binding.btnSignup.isEnabled = true
                    binding.btnSignup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
                } else {
                    binding.btnSignup.isEnabled = false
                    binding.btnSignup.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
                }
            }

        //Onclick
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this,SigninActivity::class.java))
        }

        binding.haveAccTrue.setOnClickListener {
            startActivity(Intent(this,SigninActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,LandingActivity::class.java))
        }
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.entEmail.error = if (isNotValid) "Your entered email is invalid." else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.enterUName.error = if (isNotValid) "$text must be more than 6 letters." else null
        else if (text == "Password")
            binding.entPass.error = if (isNotValid) "$text must be more than 8 characters." else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean) {
        binding.conPass.error = if (isNotValid) "Your password is not the same." else null
    }
}