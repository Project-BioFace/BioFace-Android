package com.bangkit.bioface

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.bangkit.bioface.databinding.ActivityOtpBinding
import com.bangkit.bioface.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import papaya.`in`.sendmail.SendMail
import kotlin.random.Random
import android.os.CountDownTimer
import kotlin.random.nextInt

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var auth: FirebaseAuth
    private var randomOtp: Int = 0
    private lateinit var email: String
    private lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Retrieve email and password from intent
        email = intent.getStringExtra("email").orEmpty()
        pass = intent.getStringExtra("pass").orEmpty()

        // Display email
        binding.showEmail.text = email

        // Generate and send OTP
        generateAndSendOtp()

        // Handle back button click
        binding.otpBtnBack.setOnClickListener {
            finish() // Close the activity
        }

        // Setup Resend OTP button
        setupResendOtpButton()

        // Handle OTP input navigation
        setupOtpInputNavigation()

        // Handle OTP submission
        binding.buttonSubmitOtp.setOnClickListener {
            validateAndSubmitOtp()
        }
    }

    private fun setupResendOtpButton() {
        binding.txtResendOtp.setOnClickListener {
            generateAndSendOtp() // Generate and send OTP again
            Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
            startResendOtpCountdown() // Start countdown timer
        }
    }

    private fun startResendOtpCountdown() {
        binding.txtResendOtp.isEnabled = false // Disable button during countdown
        object : CountDownTimer(30000, 1000) { // 30 seconds timer with 1-second intervals
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.txtResendOtp.text = "Resend in $secondsRemaining sec"
            }

            override fun onFinish() {
                binding.txtResendOtp.isEnabled = true // Enable button after countdown
                binding.txtResendOtp.text = "Resend OTP" // Reset button text
            }
        }.start()
    }

    private fun generateAndSendOtp() {
        // Generate a new random OTP
        randomOtp = Random.nextInt(100000..999999)
        val subject = "Bioface Account: Verify your email address"
        val body = """
            Verify your email address
    
            To finish setting up your Bioface account, we just need to make sure this email address is yours.
    
            To verify your email address, use this security code: $randomOtp
    
            If you didn't request this code, you can safely ignore this email. Someone else might have typed your email address by mistake.
    
            Thanks,
            The Bioface account team
        """.trimIndent()

        // Send the email using SendMail
        val mail = SendMail("biofacebangkit@gmail.com", "tewdxkbujgwtlbyz", email, subject, body)
        mail.execute()
    }

    private fun setupOtpInputNavigation() {
        binding.otp1.doOnTextChanged { _, _, _, _ -> if (binding.otp1.text.isNotEmpty()) binding.otp2.requestFocus() }
        binding.otp2.doOnTextChanged { _, _, _, _ -> if (binding.otp2.text.isNotEmpty()) binding.otp3.requestFocus() else binding.otp1.requestFocus() }
        binding.otp3.doOnTextChanged { _, _, _, _ -> if (binding.otp3.text.isNotEmpty()) binding.otp4.requestFocus() else binding.otp2.requestFocus() }
        binding.otp4.doOnTextChanged { _, _, _, _ -> if (binding.otp4.text.isNotEmpty()) binding.otp5.requestFocus() else binding.otp3.requestFocus() }
        binding.otp5.doOnTextChanged { _, _, _, _ -> if (binding.otp5.text.isNotEmpty()) binding.otp6.requestFocus() else binding.otp4.requestFocus() }
        binding.otp6.doOnTextChanged { _, _, _, _ -> if (binding.otp6.text.isEmpty()) binding.otp5.requestFocus() }
    }

    private fun validateAndSubmitOtp() {
        val otp = "${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}${binding.otp5.text}${binding.otp6.text}"

        when {
            otp.length < 6 -> Toast.makeText(this, "Please enter the complete OTP", Toast.LENGTH_SHORT).show()
            otp != randomOtp.toString() -> Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            else -> {
                // Register user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to MainActivity
                        val intent = Intent(this@OtpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle registration failure
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
