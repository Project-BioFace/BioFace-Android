package com.bangkit.bioface.main.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.viewmodel.FragmentDetailArticleViewModel
import com.bumptech.glide.Glide

class DetailArticlesFragment : Fragment() {

    private lateinit var viewModel: FragmentDetailArticleViewModel

    private lateinit var imageArticle: ImageView
    private lateinit var titleArticle: TextView
    private lateinit var sourceArticle: TextView
    private lateinit var contentArticle: TextView
    private lateinit var createdAtArticle: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val ARG_ARTICLE_ID = "article_id"

        fun newInstance(articleId: Int): DetailArticlesFragment {
            val fragment = DetailArticlesFragment()
            val args = Bundle()
            args.putInt(ARG_ARTICLE_ID, articleId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_articles, container, false)

        imageArticle = view.findViewById(R.id.articleImage)
        titleArticle = view.findViewById(R.id.articleTitle)
        sourceArticle = view.findViewById(R.id.articleSource)
        contentArticle = view.findViewById(R.id.articleContent)
        createdAtArticle = view.findViewById(R.id.articleCreatedAt)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleId = arguments?.getInt(ARG_ARTICLE_ID, -1) ?: -1

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(FragmentDetailArticleViewModel::class.java)

        // Observasi artikel
        viewModel.article.observe(viewLifecycleOwner) { article ->
            if (article != null) {
                updateUI(article)  // Memperbarui UI dengan artikel yang diterima
            }
        }

        // Observasi loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Ambil artikel berdasarkan ID
        if (articleId != -1) {
            viewModel.getArticleById(articleId)
        } else {
            Toast.makeText(requireContext(), "Invalid article ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(article: ArticlesItem) {
        titleArticle.text = article.title
        sourceArticle.text = "Source: ${article.source}"
        contentArticle.text = article.content
        createdAtArticle.text = "Created at: ${article.createdAt}"

        Glide.with(requireContext())
            .load(article.image)
            .into(imageArticle)
    }

}

