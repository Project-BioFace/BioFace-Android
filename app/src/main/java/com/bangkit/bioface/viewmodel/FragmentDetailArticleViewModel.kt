package com.bangkit.bioface.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.network.response.ResponseArticleDetail

import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentDetailArticleViewModel : ViewModel() {

    // LiveData untuk menyimpan artikel yang diterima
    private val _article = MutableLiveData<ArticlesItem?>()
    val article: LiveData<ArticlesItem?> = _article

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk error message
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Fungsi untuk mendapatkan artikel berdasarkan ID
    fun getArticleById(id: Int) {
        _isLoading.value = true // Menandakan bahwa data sedang dimuat
        viewModelScope.launch {
            try {
                // Memanggil API untuk mendapatkan artikel berdasarkan ID
                val response: Response<ResponseArticleDetail> = ApiClient.apiService2().getArticleById(id)

                // Mengecek apakah response berhasil dan body tidak null
                if (response.isSuccessful && response.body() != null) {
                    val article = response.body()?.data // Ambil artikel dari 'data'

                    // Jika artikel tidak null, set artikel ke LiveData
                    if (article != null) {
                        _article.value = article
                    } else {
                        _error.value = "Article data is null"
                    }

                    _isLoading.value = false // Mengubah status loading menjadi false
                    Log.d("FragmentDetailArticleViewModel", "Article data received: $article")
                } else {
                    // Jika response tidak berhasil, beri pesan error
                    _error.value = "Failed to load article"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Menangani kesalahan jika ada
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
                Log.e("FragmentDetailArticleViewModel", "Error: ${e.message}")
            }
        }
    }
}
