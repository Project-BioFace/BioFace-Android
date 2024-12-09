package com.bangkit.bioface.main.fitur

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bangkit.bioface.R
import com.bangkit.bioface.main.adapter.BannerAdapter
import com.bangkit.bioface.main.fitur.itemhome.NaturalFragment
import com.bangkit.bioface.main.fitur.itemhome.ProductFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var bannerAdapter: BannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Welcome message
        val greetingTextView: TextView = view.findViewById(R.id.tv_greeting)
        val welcomeMessageTextView: TextView = view.findViewById(R.id.tv_welcome_message)

        // Set up ViewPager for carousel
        viewPager = view.findViewById(R.id.viewPager)
        setupViewPager()

        // Fetch user data
        fetchUserData(greetingTextView, welcomeMessageTextView)

        // Optional: Auto-scroll functionality for the ViewPager
        autoScrollViewPager()
    }

    private fun fetchUserData(greetingTextView: TextView, welcomeMessageTextView: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: "Nama Tidak Ditemukan"

                        // Display the fetched data
                        greetingTextView.text = "Hi, $name"
                        welcomeMessageTextView.text = "Selamat datang di aplikasi Bioface"
                    } else {
                        greetingTextView.text = "Hi, Pengguna"
                        welcomeMessageTextView.text = "Selamat datang di aplikasi Bioface"
                    }
                }
                .addOnFailureListener { exception ->
                    greetingTextView.text = "Hi, Pengguna"
                    welcomeMessageTextView.text = "Selamat datang di aplikasi Bioface"
                    Log.e("HomeFragment", "Error fetching user data: ${exception.message}")
                }
        } else {
            greetingTextView.text = "Hi, Pengguna"
            welcomeMessageTextView.text = "Selamat datang di aplikasi Bioface"
        }
    }

    private fun setupViewPager() {
        val banners = listOf(
            R.drawable.carousel_1, // Replace with your banner images
            R.drawable.carousel_2,
            R.drawable.carousel_3,
            R.drawable.carousel_4
        )
        bannerAdapter = BannerAdapter(banners)
        viewPager.adapter = bannerAdapter
    }

    private fun autoScrollViewPager() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (viewPager.currentItem == bannerAdapter.count - 1) {
                    viewPager.currentItem = 0
                } else {
                    viewPager.currentItem += 1
                }
                handler.postDelayed(this, 3000) // Change banner every 3 seconds
            }
        }
        handler.postDelayed(runnable, 3000)
    }
}

