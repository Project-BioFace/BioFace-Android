package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.network.response.ArticlesItems
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentArticlesViewModel : ViewModel() {

    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    private var originalArticles: List<ArticlesItem> = emptyList()

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        // Mengambil data artikel dari API
        viewModelScope.launch {
            try {
                val response: Response<List<ArticlesItems>> = ApiClient.apiService.getArticles()

                // Cek apakah response berhasil
                if (response.isSuccessful) {
                    _articles.value = (response.body() ?: emptyList()) as List<ArticlesItem>?
                    originalArticles = (response.body() ?: emptyList()) as List<ArticlesItem>
                } else {
                    // Tangani jika gagal
                }
            } catch (e: Exception) {
                // Tangani error jaringan
            }
        }
    }

    fun filterArticles(query: String) {
        // Memfilter artikel berdasarkan query pencarian
        if (query.isBlank()) {
            _articles.value = originalArticles
        } else {
            _articles.value = originalArticles.filter { article ->
                article.title!!.contains(query, ignoreCase = true)
            }
        }
    }
}
