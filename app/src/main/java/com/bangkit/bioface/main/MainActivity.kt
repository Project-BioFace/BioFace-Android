package com.bangkit.bioface.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bangkit.bioface.ProfileFragment
import com.nafis.bottomnavigation.NafisBottomNavigation
import com.bangkit.bioface.R
import com.bangkit.bioface.main.fitur.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ID_HOME = 1
        private const val ID_DICTIONARY = 2
        private const val ID_SCAN = 3
        private const val ID_NEWS = 4
        private const val ID_PROFILE = 5
    }

    private lateinit var bottomNavigation: NafisBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Tampilkan HomeFragment saat pertama kali masuk
        loadFragment(HomeFragment())

        // Tambahkan item navigasi
        bottomNavigation.apply {
            add(NafisBottomNavigation.Model(ID_HOME, R.drawable.ic_home))
            add(NafisBottomNavigation.Model(ID_DICTIONARY, R.drawable.ic_dictionary))
            add(NafisBottomNavigation.Model(ID_SCAN, R.drawable.ic_scan))
            add(NafisBottomNavigation.Model(ID_NEWS, R.drawable.ic_news))
            add(NafisBottomNavigation.Model(ID_PROFILE, R.drawable.ic_profile))

            setOnClickMenuListener {
                val selectedFragment = when (it.id) {
                    ID_HOME -> HomeFragment()
                    ID_DICTIONARY -> DictionaryFragment()
                    ID_SCAN -> ScanFragment()
                    ID_NEWS -> NewsFragment()
                    ID_PROFILE -> ProfileFragment()
                    else -> HomeFragment()
                }
                loadFragment(selectedFragment)
            }

            show(ID_HOME) // Tampilkan fragment default di bottom navigation
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null) // Tambahkan ke back stack agar bisa kembali
            .commit()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        when (currentFragment) {
            is HomeFragment -> super.onBackPressed() // Tutup aplikasi jika di HomeFragment
            is NewsFragment -> {
                bottomNavigation.show(ID_HOME)
                loadFragment(HomeFragment())
            }
            is ScanFragment -> {
                bottomNavigation.show(ID_HOME)
                loadFragment(HomeFragment())
            }
            is DictionaryFragment -> {
                bottomNavigation.show(ID_HOME)
                loadFragment(HomeFragment())
            }
            is ProfileFragment -> {
                bottomNavigation.show(ID_HOME)
                loadFragment(HomeFragment())
            }
            else -> super.onBackPressed()
        }
    }
}
