package com.bangkit.bioface.main.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.ActivityLoginBinding
import com.bangkit.bioface.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.view.View

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var passwordEditText: EditText
    private lateinit var visibilityToggle: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inisialisasi View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        passwordEditText = findViewById(R.id.l_edt_password)
        visibilityToggle = findViewById(R.id.password_check)

        visibilityToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.forgotPassword.setOnClickListener {
            val forgotPassIntent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(forgotPassIntent)
        }

        binding.txtRegister.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.lEdtEmail.text.toString()
            val password = binding.lEdtPassword.text.toString()

            // Validasi input
            if (!validateInput(email, password)) return@setOnClickListener

            // Mulai proses login
            startLoginProcess()
            LoginFirebase(email, password)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility_off)
            isPasswordVisible = false
        } else {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility)
            isPasswordVisible = true
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun startLoginProcess() {
        binding.loginProgressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    private fun endLoginProcess() {
        binding.loginProgressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.lEdtEmail.error = "Email must be filled!"
            binding.lEdtEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.lEdtEmail.error = "Email not valid!"
            binding.lEdtEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.lEdtPassword.error = "Password must be filled!"
            binding.lEdtPassword.requestFocus()
            return false
        }

        if (password.length < 8) {
            binding.lEdtPassword.error = "Minimum password is 8 characters"
            binding.lEdtPassword.requestFocus()
            return false
        }

        return true
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                endLoginProcess() // Hentikan ProgressBar setelah login selesai
                if (task.isSuccessful) {
                    // Mendapatkan user ID dan navigasi ke MainActivity
                    val userId = auth.currentUser?.uid
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(userId ?: "")

                    userRef.get().addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            val user = userTask.result
                            val name = user?.getString("name") ?: "User"
                            Toast.makeText(this, "Welcome to Bioface\n$name", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

