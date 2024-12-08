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
import com.bangkit.bioface.adapter.SkincareAdapter
import com.bangkit.bioface.main.fitur.detail.DetailSkincareActivity
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.SkincareItem
import com.bangkit.bioface.viewmodel.FragmentSkincareViewModel
import kotlin.math.log

class SkincareFragment : Fragment(), SkincareAdapter.OnSkincareClickListener {

    private lateinit var viewModel: FragmentSkincareViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SkincareAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(FragmentSkincareViewModel::class.java) // Inisialisasi ViewModel
        val view = inflater.inflate(R.layout.fragment_skincare, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSkincare)

        setupRecyclerView()
        observeViewModel()

        return view
    }

    // Implementasi OnArticleClickListener
    override fun onSkincareClick(skincare: SkincareItem) {
        val skincareId = skincare.id ?: -1
        if (skincareId == -1){
            Toast.makeText(context, "Invalid Skincare Product", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("SkincareFragment", "Skincare clicked with id: $skincareId" )
        val intent = Intent(requireContext(), DetailSkincareActivity::class.java)
        intent.putExtras(DetailSkincareActivity.newInstance(skincareId))
        startActivity(intent)
    }


    private fun setupRecyclerView() {
        adapter = SkincareAdapter(this) // Pass this as the listener
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // Set layout manager
    }

    private fun observeViewModel() {
        viewModel.skincare.observe(viewLifecycleOwner, Observer { skincare ->
            if (skincare.isEmpty()) {
                Toast.makeText(context, "No skincare items found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.submitList(skincare)
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getSkincare()
    }


    fun searchSkincare(query: String) {
        viewModel.searchSkincare(query)
    }

}