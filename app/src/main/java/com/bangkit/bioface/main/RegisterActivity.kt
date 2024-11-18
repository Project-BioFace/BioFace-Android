package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inisialisasi View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()

        // Mengatur tombol kembali ke MainActivity
        binding.rBtnBack.setOnClickListener {
            // Menyelesaikan aktivitas saat ini dan kembali ke MainActivity
            finish()
        }

        // Mengatur klik pada txt_login untuk membuka LoginActivity
        binding.txtLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.rBtnRegis.setOnClickListener{
            val email = binding.rEdtEmail.text.toString()
            val password = binding.rEdtPassword.text.toString()
            val confirmPass = binding.rEdtConfirmPass.text.toString()

            // Validasi email
            if(email.isEmpty()){
                binding.rEdtEmail.error = "Email must be filled!"
                binding.rEdtEmail.requestFocus()
                return@setOnClickListener
            }

            // validasi email tidak sesuai
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.rEdtEmail.error = "Email not Valid!"
                binding.rEdtEmail.requestFocus()
                return@setOnClickListener
            }

            // validasi password
            if (password.isEmpty()){
                binding.rEdtPassword.error = "Password must be filled!"
                binding.rEdtPassword.requestFocus()
                return@setOnClickListener
            }

            // validasi panjang password
            if (password.length < 8){
                binding.rEdtPassword.error = "Minimum password is 8 character"
                binding.rEdtPassword.requestFocus()
                return@setOnClickListener
            }

            //  validasi confirm password
            if (confirmPass.isEmpty()){
                binding.rEdtConfirmPass.error = "Confirm Password must be filled!"
                binding.rEdtConfirmPass.requestFocus()
                return@setOnClickListener
            }

            if (confirmPass != password){
                binding.rEdtConfirmPass.error = "Password does not match!"
                binding.rEdtConfirmPass.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password)
        }
    }

    private fun RegisterFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}


