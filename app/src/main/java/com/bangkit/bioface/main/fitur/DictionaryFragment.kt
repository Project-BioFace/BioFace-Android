package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.bangkit.bioface.R
import com.bangkit.bioface.main.fitur.dict.HerbalFragment
import com.bangkit.bioface.main.fitur.dict.SkincareFragment
import com.bangkit.bioface.viewmodel.FragmentDictViewModel
import com.google.android.material.tabs.TabLayout

@Suppress("IMPLICIT_CAST_TO_ANY")
class DictionaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    private lateinit var viewModel: FragmentDictViewModel
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        // Tambahkan tab ke TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Herbal"))
        tabLayout.addTab(tabLayout.newTab().setText("Skincare"))

        // Load default fragment
        replaceFragment(HerbalFragment())

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
                    0 -> HerbalFragment() // Tab Bahan Alami
                    1 -> SkincareFragment() // Tab Produk
                    else -> HerbalFragment()
                }
                replaceFragment(fragment)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // Call fragment data
    private fun replaceFragment(fragment: Any) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment as Fragment)
            .commit()
    }


}