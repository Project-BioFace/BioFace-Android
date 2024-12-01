package com.bangkit.bioface.main.fitur

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bangkit.bioface.R
import com.bangkit.bioface.main.fitur.itemhome.NaturalFragment
import com.bangkit.bioface.main.fitur.itemhome.ProductFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        // Tambahkan tab ke TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Natural"))
        tabLayout.addTab(tabLayout.newTab().setText("Product"))

        // Load default fragment
        replaceFragment(NaturalFragment())

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
                    0 -> NaturalFragment() // Tab Bahan Alami
                    1 -> ProductFragment() // Tab Produk
                    else -> NaturalFragment()
                }
                replaceFragment(fragment)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Search functionality
        val searchEditText: EditText = view.findViewById(R.id.et_search)
        searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = searchEditText.text.toString()
            Toast.makeText(requireContext(), "Search: $query", Toast.LENGTH_SHORT).show()
            true
        }

    }

    // Call fragment data
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

