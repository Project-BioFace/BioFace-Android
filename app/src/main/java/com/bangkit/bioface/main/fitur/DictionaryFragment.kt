package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bioface.R
import com.bangkit.bioface.main.fitur.dict.HerbalFragment
import com.bangkit.bioface.main.fitur.dict.SkincareFragment
import com.bangkit.bioface.viewmodel.FragmentDictViewModel
import com.bangkit.bioface.viewmodel.FragmentHerbalViewModel
import com.bangkit.bioface.viewmodel.FragmentSkincareViewModel
import com.google.android.material.tabs.TabLayout


class DictionaryFragment : Fragment() {
    private lateinit var searchView: SearchView
    private var currentFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Temukan SearchView di layout
        searchView = view.findViewById(R.id.searchViewDict)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        // Tambahkan tab
        tabLayout.addTab(tabLayout.newTab().setText("Herbal"))
        tabLayout.addTab(tabLayout.newTab().setText("Skincare"))

        // Load default fragment
        currentFragment = HerbalFragment()
        replaceFragment(currentFragment!!)

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentFragment = when (tab.position) {
                    0 -> HerbalFragment()
                    1 -> SkincareFragment()
                    else -> HerbalFragment()
                }
                replaceFragment(currentFragment!!)

                // Reset search query
                searchView.setQuery("", false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Setup SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText ?: "")
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        when (currentFragment) {
            is HerbalFragment -> {
                val herbalFragment = currentFragment as HerbalFragment
                herbalFragment.searchHerbal(query)
            }
            is SkincareFragment -> {
                val skincareFragment = currentFragment as SkincareFragment
                skincareFragment.searchSkincare(query)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}