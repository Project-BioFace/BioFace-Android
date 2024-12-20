package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.R
import com.bangkit.bioface.main.auth.LoginActivity
import com.bangkit.bioface.main.auth.RegisterActivity
import com.bangkit.bioface.databinding.ActivityLandingBinding
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi View Binding
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Periksa apakah pengguna sudah login
        checkUserLogin()

        // Menangani klik tombol Login
        binding.btnLogin1.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)

            // Transisi dengan animasi
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Menangani klik tombol Register
        binding.btnRegis1.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)

            // Transisi dengan animasi
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    // Fungsi untuk memeriksa apakah pengguna sudah login
    private fun checkUserLogin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika pengguna sudah login, langsung arahkan ke MainActivity (Home)
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)

            // Transisi dengan animasi
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            finish() // Menutup LandingActivity agar tidak kembali lagi setelah masuk ke Home
        }
    }
}
