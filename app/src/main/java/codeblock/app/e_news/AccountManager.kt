package codeblock.app.e_news

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountManager : Fragment() {

    private lateinit var accountReference: DatabaseReference
    private lateinit var input: EditText // Define the input variable at the class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountReference = FirebaseDatabase.getInstance().getReference("accounts")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDelAcc = view.findViewById<Button>(R.id.btnDelAcc)
        btnDelAcc.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to delete your account?")

        builder.setPositiveButton("Yes") { _, _ ->
            showPasswordConfirmationDialog()
        }

        builder.setNegativeButton("No") { _, _ ->
            // User clicked "No", do nothing
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showPasswordConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Password")
        input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Confirm") { _, _ ->
            val password = input.text.toString()
            if (password.isNotEmpty()) {
                deleteAccount(password)
            } else {
                Toast.makeText(requireContext(), "Please enter your password.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { _, _ ->
            // User clicked "Cancel", do nothing
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteAccount(password: String) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Re-authenticate the user to ensure the provided password is correct
            val credentials = EmailAuthProvider.getCredential(currentUser.email!!, password)
            currentUser.reauthenticate(credentials)
                .addOnSuccessListener {
                    // Re-authentication successful, proceed with deleting user data from Realtime Database
                    deleteUserDataFromDatabase(currentUser.uid)
                }
                .addOnFailureListener { exception ->
                    // Re-authentication failed, show an error message
                    Toast.makeText(requireContext(), "Failed to verify your password. Please try again.", Toast.LENGTH_SHORT).show()
                    Log.e("codeblock.app.e_news.AccountManager", "Error re-authenticating user: ${exception.message}")
                }
        } else {
            // If the current user is null, show an error message
            Toast.makeText(requireContext(), "Failed to delete account. User not found.", Toast.LENGTH_SHORT).show()
            Log.e("codeblock.app.e_news.AccountManager", "Error deleting account: User is null.")
        }
    }

    private fun deleteUserDataFromDatabase(userId: String) {
        Log.d("codeblock.app.e_news.AccountManager", "User ID to delete: $userId")
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

        usersRef.child(userId).removeValue()
            .addOnSuccessListener {
                // User data deleted successfully, now delete the user from Firebase Authentication
                deleteFirebaseUser()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to delete account data. Please try again.", Toast.LENGTH_SHORT).show()
                Log.e("codeblock.app.e_news.AccountManager", "Error deleting account data: ${exception.message}")
            }
    }

    private fun deleteFirebaseUser() {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            currentUser.delete()
                .addOnSuccessListener {
                    // User deleted from Firebase Authentication
                    navigateToSignUpActivity()
                }
                .addOnFailureListener { exception ->
                    // User deletion failed, show an error message
                    Toast.makeText(requireContext(), "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show()
                    Log.e("codeblock.app.e_news.AccountManager", "Error deleting user from Firebase Authentication: ${exception.message}")
                }
        } else {
            // If the current user is null, show an error message
            Toast.makeText(requireContext(), "Failed to delete account. User not found.", Toast.LENGTH_SHORT).show()
            Log.e("codeblock.app.e_news.AccountManager", "Error deleting user from Firebase Authentication: User is null.")
        }
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(requireContext(), LandingActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}