package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ResponseSkincareList
import com.bangkit.bioface.network.response.SkincareItem
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentSkincareViewModel : ViewModel() {

    private val _allSkincare = MutableLiveData<List<SkincareItem>>() // Data asli
    private val _filteredSkincare = MutableLiveData<List<SkincareItem>>() // Data hasil filter
    private val _errorMessage = MutableLiveData<String>() // Pesan error
    val skincare: LiveData<List<SkincareItem>> = _filteredSkincare // Artikel yang ditampilkan
    val errorMessage: LiveData<String> = _errorMessage // Pesan error

    fun getSkincare() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseSkincareList> = ApiClient.apiService.getSkincareItems()
                if (response.isSuccessful && response.body() != null) {
                    val skincareList = response.body()?.data ?: emptyList()
                    if (skincareList.isEmpty()) {
                        _errorMessage.value = "Skincare not found"
                    } else {
                        _allSkincare.value = skincareList
                        _filteredSkincare.value = skincareList // Menampilkan artikel setelah diambil dari API
                    }
                } else {
                    _errorMessage.value = "Failed to load Skincare Product"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
