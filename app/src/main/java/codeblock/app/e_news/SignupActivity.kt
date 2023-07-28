package codeblock.app.e_news

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import codeblock.app.e_news.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Authentication
        auth = FirebaseAuth.getInstance()

        // Username Validation
        val usernameStream = RxTextView.textChanges(binding.enterUName)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

        // Email Validation
        val emailStream = RxTextView.textChanges(binding.entEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }

        // Password Validation
        val passStream = RxTextView.textChanges(binding.entPass)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

        // Terms and Conditions
        val termsAndConditionsStream = RxCompoundButton.checkedChanges(binding.checkTerms)
            .skipInitialValue()

        // Button enable
        val invalidFieldStream = io.reactivex.Observable.combineLatest(
            usernameStream,
            emailStream,
            passStream,
            termsAndConditionsStream
        ) { usernameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean, termsAndConditionsChecked: Boolean ->
            !usernameInvalid && !emailInvalid && !passwordInvalid && termsAndConditionsChecked
        }
        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btnSignup.isEnabled = true
                binding.btnSignup.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.green)
            } else {
                binding.btnSignup.isEnabled = false
                binding.btnSignup.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        // Onclick
        binding.btnSignup.setOnClickListener {
            val email = binding.entEmail.text.toString().trim()
            val password = binding.entPass.text.toString().trim()
            signupUser(email, password)
        }

        binding.haveAccTrue.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.entEmail.error = if (isNotValid) "Your entered email is invalid." else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.enterUName.error =
                if (isNotValid) "$text must be more than 6 letters." else null
        else if (text == "Password")
            binding.entPass.error =
                if (isNotValid) "$text must be more than 8 characters." else null
    }

    private fun saveUserToFirebaseDatabase(email: String, username: String, password: String) {
        val userId = auth.currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        userId?.let {
            val userData = HashMap<String, Any>()
            userData["email"] = email
            userData["username"] = username
            userData["password"] = password

            usersRef.child(userId).setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "User data saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save user data to Firebase.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun signupUser(email: String, password: String) {
        // Get the username inputted by the user during sign-up
        val username = binding.enterUName.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            // Save the username to SharedPreferences
                            saveUsernameToSharedPreferences(username)

                            // Save the username to Firebase Realtime Database
                            saveUserToFirebaseDatabase(email, username, password)

                            // Show a message to the user indicating that they need to verify their email.
                            Toast.makeText(
                                this,
                                "Account created successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            // Redirect the user to the CheckEmailActivity for further instructions.
                            startActivity(Intent(this, CheckEmailActivity::class.java))
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to send verification email. Please try again later.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }
}
