package com.bangkit.bioface.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.main.auth.LoginActivity
import com.bangkit.bioface.main.auth.RegisterActivity
import com.bangkit.bioface.databinding.ActivityLandingBinding


class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inisialisasi View Binding
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menangani klik tombol Login
        binding.btnLogin1.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Menangani klik tombol Register
        binding.btnRegis1.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}
