package com.bangkit.bioface.main.fitur.itemhome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.main.adapter.HerbalAdapter
import com.bangkit.bioface.main.adapter.HerbalItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NaturalFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var herbalAdapter: HerbalAdapter
    private val herbalItems = mutableListOf<HerbalItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_natural, container, false)

        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView = view.findViewById(R.id.rv_herbs)
        tvEmpty = view.findViewById(R.id.tv_empty)

        // Inisialisasi adapter dengan data kosong
        herbalAdapter = HerbalAdapter(herbalItems) { item ->
            Toast.makeText(requireContext(), "${item.name} Favorited!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = herbalAdapter

        // Panggil fungsi untuk mendapatkan data herbal
        fetchNaturalHerbs()
        return view
    }

    private fun fetchNaturalHerbs() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        ApiClient.apiService().getNaturalHerbs().enqueue(object : Callback<List<HerbalItem>> {
            override fun onResponse(
                call: Call<List<HerbalItem>>,
                response: Response<List<HerbalItem>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val herbs = response.body()!!
                    herbalItems.clear()
                    herbalItems.addAll(herbs)
                    herbalAdapter.notifyDataSetChanged()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<HerbalItem>>, t: Throwable) {
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                Toast.makeText(context, "Failed to load data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}