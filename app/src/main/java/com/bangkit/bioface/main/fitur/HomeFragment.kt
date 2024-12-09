package com.bangkit.bioface.main.fitur

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bangkit.bioface.R
import com.bangkit.bioface.adapter.HerbalHomeAdapter
import com.bangkit.bioface.adapter.SkincareHomeAdapter
import com.bangkit.bioface.main.adapter.BannerAdapter
import com.bangkit.bioface.main.fitur.detail.DetailHerbalActivity
import com.bangkit.bioface.main.fitur.detail.DetailSkincareActivity
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.SkincareItem
import com.bangkit.bioface.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(),
    HerbalHomeAdapter.OnDictClickListener,
    SkincareHomeAdapter.OnSkincareClickListener {

    private lateinit var viewPager: ViewPager
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var herbalRecyclerView: RecyclerView
    private lateinit var skincareRecyclerView: RecyclerView
    private lateinit var herbalAdapter: HerbalHomeAdapter
    private lateinit var skincareAdapter: SkincareHomeAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Welcome message
        val greetingTextView: TextView = view.findViewById(R.id.tv_greeting)
        val welcomeMessageTextView: TextView = view.findViewById(R.id.tv_welcome_message)

        // Set up ViewPager for carousel
        viewPager = view.findViewById(R.id.viewPager)
        setupViewPager()

        // Set up Herbal RecyclerView
        herbalRecyclerView = view.findViewById(R.id.recyclerViewHerbalHome)
        setupHerbalRecyclerView()

        // Set up Skincare RecyclerView
        skincareRecyclerView = view.findViewById(R.id.recyclerViewSkincareHome)
        setupSkincareRecyclerView()

        // Fetch user data
        fetchUserData(greetingTextView, welcomeMessageTextView)

        // Observe herbal and skincare data
        observeData()

        // Optional: Auto-scroll functionality for the ViewPager
        autoScrollViewPager()
    }

    private fun setupHerbalRecyclerView() {
        herbalAdapter = HerbalHomeAdapter(this)
        herbalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        herbalRecyclerView.adapter = herbalAdapter
    }

    private fun setupSkincareRecyclerView() {
        skincareAdapter = SkincareHomeAdapter(this)
        skincareRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        skincareRecyclerView.adapter = skincareAdapter
    }

    private fun observeData() {
        // Observe herbal data
        viewModel.herbalList.observe(viewLifecycleOwner) { herbalList ->
            herbalAdapter.submitList(herbalList)
        }

        // Observe skincare data
        viewModel.skincareList.observe(viewLifecycleOwner) { skincareList ->
            skincareAdapter.submitList(skincareList)
        }
    }

    // Implement click listeners
    override fun onDictClick(dict: DictItem) {
        val intent = Intent(requireContext(), DetailHerbalActivity::class.java).apply {
            putExtra(DetailHerbalActivity.ARG_HERBAL_ID, dict.id)
        }
        startActivity(intent)
    }

    override fun onSkincareClick(skincare: SkincareItem) {
        val intent = Intent(requireContext(), DetailSkincareActivity::class.java).apply {
            putExtra(DetailSkincareActivity.ARG_SKINCARE_ID, skincare.id)
        }
        startActivity(intent)
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
                        welcomeMessageTextView.text = "Welcome to Bioface"
                    } else {
                        greetingTextView.text = "Hi, Pengguna"
                        welcomeMessageTextView.text = "Welcome to Bioface"
                    }
                }
                .addOnFailureListener { exception ->
                    greetingTextView.text = "Hi, Pengguna"
                    welcomeMessageTextView.text = "Welcome to Bioface"
                    Log.e("HomeFragment", "Error fetching user data: ${exception.message}")
                }
        } else {
            greetingTextView.text = "Hi, Pengguna"
            welcomeMessageTextView.text = "Selamat datang di aplikasi Bioface"
        }
    }

    private fun setupViewPager() {
        // Daftar gambar yang akan ditampilkan
        val banners = listOf(
            R.drawable.carousel_1, // Gambar pertama
            R.drawable.carousel_2, // Gambar kedua
            R.drawable.carousel_3, // Gambar ketiga
            R.drawable.carousel_4  // Gambar keempat
        )

        // Setel adapter untuk ViewPager
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
                handler.postDelayed(this, 7000) // Change banner every 3 seconds
            }
        }
        handler.postDelayed(runnable, 7000)
    }
}

