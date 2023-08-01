package codeblock.app.e_news

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Profile : Fragment() {

    private lateinit var enterUName: TextInputEditText
    private lateinit var entEmail: TextInputEditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnChangePass: Button
    private lateinit var enterName: TextInputLayout
    private lateinit var enterEmail: TextInputLayout
    private lateinit var nav_sidebarView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private lateinit var headerUsernameTextView: TextView
    private lateinit var headerEmailTextView: TextView

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        enterUName = rootView.findViewById(R.id.enterUName)
        entEmail = rootView.findViewById(R.id.entEmail)
        btnSaveChanges = rootView.findViewById(R.id.btnSaveChanges)
        btnChangePass = rootView.findViewById(R.id.btnChangePass)
        enterName = rootView.findViewById(R.id.enterName)
        enterEmail = rootView.findViewById(R.id.enterEmail)

        // Initialize FirebaseAuth and Realtime Database
        auth = FirebaseAuth.getInstance()
        userDatabase = FirebaseDatabase.getInstance().reference.child("users")
        userDatabase = FirebaseDatabase.getInstance().reference.child("email")

        // Retrieve shared preferences for username
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", 0)

        // Set current username to the TextInputEditText
        enterUName.setText(sharedPreferences.getString("username", "User"))
        entEmail.setText(sharedPreferences.getString("email", "email@gmail.com"))

        // Handle Save Changes, Change Password, and Circle Image button click
        btnSaveChanges.setOnClickListener {
            showPasswordConfirmationDialog()
        }
        btnChangePass.setOnClickListener {
            showChangePasswordPrompt()
        }

        // Find the headerUsernameTextView in the header layout
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_sidebarView)
        val headerView = navigationView.getHeaderView(0)

        headerUsernameTextView = headerView.findViewById(R.id.nav_userName)
        headerEmailTextView = headerView.findViewById(R.id.nav_email)

        // Initialize the nav_sidebarView variable
        nav_sidebarView = requireActivity().findViewById(R.id.nav_sidebarView)

        return rootView
    }

    private fun saveUsernameLocally(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    private fun saveEmailLocally(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

    private fun showPasswordConfirmationDialog() {
        val passwordInputDialog = AlertDialog.Builder(requireContext())
        val inputPassword = EditText(requireContext())

        inputPassword.hint = "Enter your password"
        inputPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        passwordInputDialog.setView(inputPassword)
        passwordInputDialog.setTitle("Confirm Password")
        passwordInputDialog.setPositiveButton("Confirm") { _, _ ->
            val password = inputPassword.text.toString().trim()
            confirmPasswordAndUpdateChanges(password)
        }
        passwordInputDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        passwordInputDialog.show()

        passwordInputDialog.setPositiveButton("Confirm") { _, _ ->
            val password = inputPassword.text.toString().trim()
            confirmPasswordAndUpdateChanges(password)
        }
    }

    private fun confirmPasswordAndUpdateChanges(password: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Create an EmailAuthCredential with the provided email and password
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)

            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Reauthentication succeeded, proceed with updating email and username
                        val newUsername = enterUName.text.toString().trim()
                        val newEmail = entEmail.text.toString().trim()

                        if (newUsername.isEmpty()) {
                            showToast("Username cannot be empty")
                            return@addOnCompleteListener
                        }

                        if (newEmail.isEmpty()) {
                            showToast("Email cannot be empty")
                            return@addOnCompleteListener
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                            showToast("Invalid email format")
                            return@addOnCompleteListener
                        }

                        // Update email in Firebase
                        currentUser.updateEmail(newEmail)
                            .addOnCompleteListener { emailUpdateTask ->
                                if (emailUpdateTask.isSuccessful) {
                                    // Update username in Firebase
                                    currentUser.updateProfile(
                                        UserProfileChangeRequest.Builder()
                                            .setDisplayName(newUsername)
                                            .setDisplayName(newEmail)
                                            .build()
                                    ).addOnCompleteListener { profileUpdateTask ->
                                        if (profileUpdateTask.isSuccessful) {
                                            // Save the new username to the Realtime Database
                                            val userId = currentUser.uid
                                            val usersRef = Firebase.database.reference.child("users")
                                            usersRef.child(userId).child("username")
                                                .setValue(newUsername)
                                                .addOnCompleteListener { dbTask ->
                                                    if (dbTask.isSuccessful) {
                                                        showToast("Changes saved successfully.")

                                                        // Save the new username in SharedPreferences
                                                        saveUsernameLocally(newUsername)

                                                        // Update header username
                                                        val headerView = nav_sidebarView.getHeaderView(0)
                                                        val headerUsernameTextView = headerView.findViewById<TextView>(R.id.nav_userName)
                                                        headerUsernameTextView.text = newUsername

                                                        // Update email in Realtime Database
                                                        usersRef.child(userId).child("email")
                                                            .setValue(newEmail)
                                                            .addOnCompleteListener { emailDbTask ->
                                                                if (emailDbTask.isSuccessful) {
                                                                    // Send verification email to the new email
                                                                    currentUser.sendEmailVerification()
                                                                        .addOnCompleteListener { verificationTask ->
                                                                            if (verificationTask.isSuccessful) {
                                                                                showToast("Success! Details changed and verification email sent.")

                                                                                saveEmailLocally(newEmail)

                                                                                // Update header email

                                                                                val headerView = nav_sidebarView.getHeaderView(0)
                                                                                val headerEmailTextView = headerView.findViewById<TextView>(R.id.nav_email)
                                                                                headerEmailTextView.text = newEmail
                                                                            } else {
                                                                                showToast("Failed to send verification email.")
                                                                            }
                                                                        }
                                                                } else {
                                                                    showToast("Failed to update email in the database.")
                                                                }
                                                            }
                                                    } else {
                                                        showToast("Failed to save changes to database.")
                                                    }
                                                }
                                        } else {
                                            showToast("Failed to update profile.")
                                        }
                                    }
                                } else {
                                    showToast("Failed to update email.")
                                }
                            }
                    } else {
                        showToast("Incorrect password. Please try again.")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showChangePasswordPrompt() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val newPasswordInput = TextInputEditText(requireContext())
        newPasswordInput.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(newPasswordInput)

        builder.setPositiveButton("Change") { dialog, _ ->
            val newPassword = newPasswordInput.text.toString().trim()

            if (newPassword.isEmpty()) {
                showToast("New password cannot be empty")
                return@setPositiveButton
            }

            if (newPassword.length < 6) {
                showToast("New password must be at least 6 characters")
                return@setPositiveButton
            }

            reauthenticateUserForPasswordChange(newPassword)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun reauthenticateUserForPasswordChange(newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            val passwordPromptBuilder = AlertDialog.Builder(requireContext())
            passwordPromptBuilder.setTitle("Enter Current Password")

            val passwordInput = TextInputEditText(requireContext())
            passwordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordPromptBuilder.setView(passwordInput)

            passwordPromptBuilder.setPositiveButton("OK") { _, _ ->
                val currentPassword = passwordInput.text.toString().trim()
                if (currentPassword.isNotEmpty()) {
                    reauthenticateUser(currentPassword, newPassword)
                } else {
                    showToast("Current password cannot be empty")
                }
            }

            passwordPromptBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            passwordPromptBuilder.show()
        }
    }

    private fun reauthenticateUser(currentPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        changePasswordAndUpdateDatabase(newPassword)
                    } else {
                        showToast("Incorrect password. Please try again.")
                    }
                }
        }
    }

    private fun changePasswordAndUpdateDatabase(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Password changed successfully")
                // Update the new password in the Realtime Database
                val userId = user.uid
                val usersRef = Firebase.database.reference.child("users")
                usersRef.child(userId).child("password")
                    .setValue(newPassword)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            //success
                        } else {
                            //failed
                        }
                    }
            } else {
                showToast("Failed to change password: ${task.exception?.message}")
            }
        }
    }
}

