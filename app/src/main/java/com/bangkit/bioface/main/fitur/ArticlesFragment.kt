package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bioface.R
import com.bangkit.bioface.main.adapter.ArticleAdapter
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.viewmodel.FragmentArticlesViewModel

class ArticlesFragment : Fragment(), ArticleAdapter.OnArticleClickListener {

    private lateinit var viewModel: FragmentArticlesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FragmentArticlesViewModel::class.java) // Inisialisasi ViewModel
        val view = inflater.inflate(R.layout.fragment_articles, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewArticles)
        searchView = view.findViewById(R.id.searchViewArticles)

        setupRecyclerView()
        observeViewModel()
        setupSearchView()

        return view
    }

    // Implementasi OnArticleClickListener
    override fun onArticleClick(article: ArticlesItem) {
        val articleId = article.id ?: -1
        Log.d("ArticlesFragment", "Article clicked with ID: $articleId") // Debugging log
        val detailFragment = DetailArticlesFragment.newInstance(articleId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun setupRecyclerView() {
        adapter = ArticleAdapter(this) // Pass this as the listener
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // Set layout manager
    }

    private fun observeViewModel() {
        viewModel.articles.observe(viewLifecycleOwner, Observer { articles ->
            adapter.submitList(articles)
        })
        viewModel.getArticles() // Memanggil fungsi untuk mendapatkan artikel
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterArticles(newText ?: "")
                return true
            }
        })
    }
}