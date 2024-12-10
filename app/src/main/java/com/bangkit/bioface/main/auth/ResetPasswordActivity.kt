package com.bangkit.bioface.main.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.bioface.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var sendResetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()


        emailEditText = findViewById(R.id.edt_reset_email)
        sendResetButton = findViewById(R.id.btn_send_reset)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sendResetButton.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            emailEditText.requestFocus()
            return
        }

        sendResetButton.isEnabled = false

        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(
                        this,
                        "Email tidak terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendResetButton.isEnabled = true
                } else {
                    sendPasswordResetEmail(email)
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Gagal memeriksa email: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                sendResetButton.isEnabled = true
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val toast = Toast.makeText(
                        this,
                        "The password reset email has been sent. Please check your inbox.",
                        Toast.LENGTH_LONG
                    )

                    toast.view?.setOnClickListener {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                        toast.cancel()
                    }

                    toast.show()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal mengirim email reset: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                sendResetButton.isEnabled = true
            }
    }
}