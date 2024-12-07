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
import com.bangkit.bioface.main.fitur.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        visibilityToggle.setOnClickListener{
            togglePasswordVisibility()
        }

        binding.forgotPassword.setOnClickListener{
            val forgotPassIntent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(forgotPassIntent)
        }


        // Mengatur klik pada txt_register untuk membuka RegisterActivity
        binding.txtRegister.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.lEdtEmail.text.toString()
            val password = binding.lEdtPassword.text.toString()

            // Validasi email
            if(email.isEmpty()){
                binding.lEdtEmail.error = "Email must be filled!"
                binding.lEdtEmail.requestFocus()
                return@setOnClickListener
            }

            // validasi email tidak sesuai
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.lEdtEmail.error = "Email not Valid!"
                binding.lEdtEmail.requestFocus()
                return@setOnClickListener
            }

            // validasi password
            if (password.isEmpty()){
                binding.lEdtPassword.error = "Password must be filled!"
                binding.lEdtPassword.requestFocus()
                return@setOnClickListener
            }

            // validasi panjang password
            if (password.length < 8){
                binding.lEdtPassword.error = "Minimum password is 8 character"
                binding.lEdtPassword.requestFocus()
                return@setOnClickListener
            }


            LoginFirebase(email, password)
        }

    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility_off)
            isPasswordVisible = false
        } else {
            // Show password
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility)
            isPasswordVisible = true
        }

        // Move cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Mendapatkan user ID setelah login berhasil
                    val userId = auth.currentUser?.uid

                    // Mengakses Firestore untuk mengambil data pengguna
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(userId ?: "")

                    userRef.get().addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            // Mendapatkan data nama dari Firestore
                            val user = userTask.result
                            val name = user?.getString("name") ?: "User" // Ganti 'name' dengan field yang sesuai di Firestore

                            // Menampilkan pesan toast dengan nama pengguna
                            Toast.makeText(this, "Welcome to Bioface\n$name", Toast.LENGTH_SHORT).show()

                            // Menavigasi ke MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Menangani error jika gagal mengambil data
                            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Menampilkan pesan toast jika login gagal
                    Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
