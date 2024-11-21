package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
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

        // Tombol kembali
        binding.rBtnBack.setOnClickListener {
            finish()
        }

        // Tombol daftar
        binding.rBtnRegis.setOnClickListener {
            val email = binding.rEdtEmail.text.toString()
            val password = binding.rEdtPassword.text.toString()
            val confirmPassword = binding.rEdtConfirmPass.text.toString()

            when {
                email.isEmpty() -> {
                    binding.rEdtEmail.error = "Enter Email"
                    binding.rEdtEmail.requestFocus()
                }
                password.isEmpty() -> {
                    binding.rEdtPassword.error = "Enter Password"
                    binding.rEdtPassword.requestFocus()
                }
                confirmPassword.isEmpty() -> {
                    binding.rEdtConfirmPass.error = "Enter Password Again"
                    binding.rEdtConfirmPass.requestFocus()
                }
                password != confirmPassword -> {
                    binding.rEdtConfirmPass.error = "Passwords must match"
                    binding.rEdtConfirmPass.requestFocus()
                }
                else -> {
                    // Lanjut ke OTP Activity
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
