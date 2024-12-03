package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.bangkit.bioface.adapter.ArticleAdapter
import com.bangkit.bioface.databinding.FragmentArticlesBinding
import com.bangkit.bioface.viewmodel.FragmentArticlesViewModel

class ArticlesFragment : Fragment() {

    private val binding by lazy { FragmentArticlesBinding.inflate(layoutInflater) }
    private val viewModel: FragmentArticlesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView dan adapter
        val articleAdapter = ArticleAdapter()
        binding.recyclerViewArticles.adapter = articleAdapter

        // Observe LiveData dari ViewModel
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
        }

        // Setup SearchView untuk filter artikel
        binding.searchViewArticles.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterArticles(newText.orEmpty())
                return true
            }
        })
    }
}
