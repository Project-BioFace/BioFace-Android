package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentHistoryBinding
import com.bangkit.bioface.main.adapter.HistoryAdapter
import com.bangkit.bioface.main.adapter.ResultItem

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val scanResults = ArrayList<ResultItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter(scanResults) { scanResult ->
            // Navigasi ke DetailResultFragment saat item diklik
            navigateToDetailResult(scanResult)
        }
        //binding.recyclerView.adapter = adapter
        //binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load data dari penyimpanan (jika ada)
        loadHistory()
    }

    private fun loadHistory() {
        // Implementasikan logika untuk memuat data dari penyimpanan
        // Misalnya, jika menggunakan SharedPreferences atau database
    }

    private fun navigateToDetailResult(scanResult: ResultItem) {
        val detailFragment = DetailResultFragment.newInstance(scanResult)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    fun addScanResult(scanResult: ResultItem) {
        scanResults.add(scanResult)
        adapter.notifyItemInserted(scanResults.size - 1)
        // Simpan data ke penyimpanan (jika perlu)
    }
}
