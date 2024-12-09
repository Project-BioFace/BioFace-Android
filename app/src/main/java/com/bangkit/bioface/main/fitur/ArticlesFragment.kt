package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.main.adapter.ArticleAdapter
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.viewmodel.FragmentArticlesViewModel

class ArticlesFragment : Fragment(), ArticleAdapter.OnArticleClickListener {

    private lateinit var viewModel: FragmentArticlesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: ArticleAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingContainer: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FragmentArticlesViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_articles, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewArticles)
        searchView = view.findViewById(R.id.searchViewArticles)
        progressBar = view.findViewById(R.id.progressBarLoading)
        loadingContainer = view.findViewById(R.id.loadingContainer)

        setupRecyclerView()
        observeViewModel()
        setupSearchView()

        return view
    }

    override fun onArticleClick(article: ArticlesItem) {
        val articleId = article.id ?: -1
        Log.d("ArticlesFragment", "Article clicked with ID: $articleId")
        val detailFragment = DetailArticlesFragment.newInstance(articleId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeViewModel() {
        viewModel.articles.observe(viewLifecycleOwner, Observer { articles ->
            if (articles.isEmpty()) {
                Toast.makeText(context, "No articles found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.submitList(articles)
            }
            toggleLoading(false)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            toggleLoading(isLoading)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getArticles()
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

    private fun toggleLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            loadingContainer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
