package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.network.response.ResponseArticlesList
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentArticlesViewModel : ViewModel() {

    private val _allArticles = MutableLiveData<List<ArticlesItem>>() // Data asli
    private val _filteredArticles = MutableLiveData<List<ArticlesItem>>() // Data hasil filter
    val articles: LiveData<List<ArticlesItem>> = _filteredArticles // Artikel yang ditampilkan

    fun getArticles() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseArticlesList> = ApiClient.apiService.getArticles()
                if (response.isSuccessful && response.body() != null) {
                    val articlesList = response.body()?.data ?: emptyList()
                    _allArticles.value = articlesList
                    _filteredArticles.value = articlesList
                } else {
                    _allArticles.value = emptyList()
                    _filteredArticles.value = emptyList()
                }
            } catch (e: Exception) {
                _allArticles.value = emptyList()
                _filteredArticles.value = emptyList()
            }
        }
    }

    fun filterArticles(query: String) {
        val articles = _allArticles.value ?: emptyList()
        _filteredArticles.value = if (query.isBlank()) {
            articles // Tampilkan semua artikel jika query kosong
        } else {
            articles.filter { article ->
                article.title?.contains(query, ignoreCase = true) == true ||
                        article.source?.contains(query, ignoreCase = true) == true
            }
        }
    }
}
