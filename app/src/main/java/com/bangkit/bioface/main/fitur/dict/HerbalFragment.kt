package com.bangkit.bioface.main.fitur.dict

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bioface.R
import com.bangkit.bioface.adapter.HerbalAdapter
import com.bangkit.bioface.main.fitur.detail.DetailHerbalActivity
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.viewmodel.FragmentHerbalViewModel

class HerbalFragment : Fragment(), HerbalAdapter.OnDictClickListener {

    private lateinit var viewModel: FragmentHerbalViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HerbalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FragmentHerbalViewModel::class.java) // Inisialisasi ViewModel
        val view = inflater.inflate(R.layout.fragment_herbal, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewHerbal)

        setupRecyclerView()
        observeViewModel()

        return view
    }

    // Implementasi OnArticleClickListener
    override fun onDictClick(herbal: DictItem) {
        val herbalId = herbal.id ?: -1
        if (herbalId == -1){
            Toast.makeText(context, "Invalid HerbalProduct", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("HerbalFragment", "Herbal clicked with id: $herbalId" )
        val intent = Intent(requireContext(), DetailHerbalActivity::class.java)
        intent.putExtras(DetailHerbalActivity.newInstance(herbalId))
        startActivity(intent)
    }


    private fun setupRecyclerView() {
        adapter = HerbalAdapter(this) // Pass this as the listener
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // Set layout manager
    }

    private fun observeViewModel() {
        viewModel.herbal.observe(viewLifecycleOwner, Observer { herbal ->
            adapter.submitList(herbal)
        })
        viewModel.getHerbal() // Memanggil fungsi untuk mendapatkan artikel
    }

    fun searchHerbal(query: String) {
        viewModel.searchHerbal(query)
    }

}