package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inisialisasi View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Mengatur tombol kembali ke MainActivity
        binding.lBtnBack.setOnClickListener {
            // Menyelesaikan aktivitas saat ini dan kembali ke MainActivity
            finish()
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

    private fun LoginFirebase(email: String, password: String,) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, "Welcome to Bioface $email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
    }
}
