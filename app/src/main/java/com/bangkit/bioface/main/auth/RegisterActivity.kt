package com.bangkit.bioface.main.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Menyembunyikan action bar
        supportActionBar?.hide()

        binding.txtLogin.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Tombol daftar
        binding.rBtnRegis.setOnClickListener {
            val name = binding.rEdtName.text.toString()
            val email = binding.rEdtEmail.text.toString().trim().lowercase() // Normalisasi email
            val password = binding.rEdtPassword.text.toString()
            val confirmPassword = binding.rEdtConfirmPass.text.toString()

            // Validasi input
            when {
                name.isEmpty() -> {
                    binding.rEdtName.error = "Name must be filled!"
                    binding.rEdtName.requestFocus()
                }

                email.isEmpty() -> {
                    binding.rEdtEmail.error = "Email must be filled!"
                    binding.rEdtEmail.requestFocus()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.rEdtEmail.error = "Invalid email format!"
                    binding.rEdtEmail.requestFocus()
                }

                password.isEmpty() -> {
                    binding.rEdtPassword.error = "Password must be filled!"
                    binding.rEdtPassword.requestFocus()
                }

                confirmPassword.isEmpty() -> {
                    binding.rEdtConfirmPass.error = "Please confirm your password!"
                    binding.rEdtConfirmPass.requestFocus()
                }

                password != confirmPassword -> {
                    binding.rEdtConfirmPass.error = "Passwords do not match!"
                    binding.rEdtConfirmPass.requestFocus()
                }

                password.length < 8 -> {
                    binding.rEdtPassword.error = "Password must be at least 8 characters!"
                    binding.rEdtPassword.requestFocus()
                }

                else -> {
                    // Validasi jika email sudah ada
                    checkEmailBeforeRegister(email, password, name)
                }
            }
        }
    }

    private fun checkEmailBeforeRegister(email: String, password: String, name: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        // Email belum terdaftar, lanjutkan ke proses registrasi
                        registerUser(email, password, name)
                    } else {
                        // Email sudah terdaftar
                        showError("Email already registered!")
                        binding.rEdtEmail.error = "Email already registered!"
                        binding.rEdtEmail.requestFocus()
                    }
                } else {
                    showError("Failed to check email: ${task.exception?.message}")
                }
            }
            .addOnFailureListener {
                showError("An error occurred: ${it.message}")
            }
    }

    private fun registerUser(email: String, password: String, name: String) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Error: Email or password is missing!")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, name, email, password)
                    } else {
                        showError("User ID not found!")
                    }
                } else {
                    val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                    val errorMessage = when (errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> "Email sudah digunakan"
                        "ERROR_INVALID_EMAIL" -> "Format email tidak valid"
                        else -> task.exception?.message ?: "Terjadi kesalahan"
                    }
                    showError(errorMessage)
                    Log.e("RegisterError", "Error registering user: ${task.exception}")
                }
            }
    }

    private fun saveUserToFirestore(userId: String, name: String, email: String, password: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userMap = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        firestore.collection("users").document(userId).set(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    proceedToOtpActivity(email, password)
                } else {
                    showError("Failed to save user data: ${task.exception?.message}")
                }
            }
            .addOnFailureListener {
                showError("An error occurred while saving data: ${it.message}")
            }
    }

    private fun proceedToOtpActivity(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Error: Email or password is missing!")
            return
        }

        Log.d("RegisterActivity", "Proceeding to OTP with email: $email and password.")
        val intent = Intent(this, OtpActivity::class.java).apply {
            putExtra("email", email)
            putExtra("pass", password)
        }
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
