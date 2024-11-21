package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.OtpActivity
import com.bangkit.bioface.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menyembunyikan action bar
        supportActionBar?.hide()

        // Navigasi ke LoginActivity
        binding.txtLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Tombol daftar
        binding.rBtnRegis.setOnClickListener {
            val name = binding.rEdtName.text.toString()
            val email = binding.rEdtEmail.text.toString()
            val password = binding.rEdtPassword.text.toString()
            val confirmPassword = binding.rEdtConfirmPass.text.toString()

            when {
                // Validasi nama kosong
                name.isEmpty() -> {
                    binding.rEdtName.error = "Name must be filled!"
                    binding.rEdtName.requestFocus()
                }

                // Validasi email kosong
                email.isEmpty() -> {
                    binding.rEdtEmail.error = "Email must be filled!"
                    binding.rEdtEmail.requestFocus()
                }

                // Validasi email tidak valid
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.rEdtEmail.error = "Email not valid!"
                    binding.rEdtEmail.requestFocus()
                }

                // Validasi password kosong
                password.isEmpty() -> {
                    binding.rEdtPassword.error = "Password must be filled!"
                    binding.rEdtPassword.requestFocus()
                }

                // Validasi confirm password kosong
                confirmPassword.isEmpty() -> {
                    binding.rEdtConfirmPass.error = "Enter Password Again"
                    binding.rEdtConfirmPass.requestFocus()
                }

                // Validasi password dan confirm password tidak cocok
                password != confirmPassword -> {
                    binding.rEdtConfirmPass.error = "Passwords must match"
                    binding.rEdtConfirmPass.requestFocus()
                }

                // Validasi panjang password kurang dari 8 karakter
                password.length < 8 -> {
                    binding.rEdtPassword.error = "Minimum password is 8 characters"
                    binding.rEdtPassword.requestFocus()
                }

                // Jika semua validasi lolos, lanjutkan ke OTP Activity
                else -> {
                    val intent = Intent(this, OtpActivity::class.java).apply {
                        putExtra("email", email)
                        putExtra("pass", password)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}
