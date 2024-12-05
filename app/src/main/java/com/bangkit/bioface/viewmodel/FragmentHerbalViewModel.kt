package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.ResponseDictList
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentHerbalViewModel : ViewModel() {

    private val _allHerbal = MutableLiveData<List<DictItem>>() // Data asli
    private val _filteredHerbal = MutableLiveData<List<DictItem>>() // Data hasil filter
    private val _errorMessage = MutableLiveData<String>() // Pesan error
    val herbal: LiveData<List<DictItem>> = _filteredHerbal // Artikel yang ditampilkan
    val errorMessage: LiveData<String> = _errorMessage // Pesan error

    fun getHerbal() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseDictList> = ApiClient.apiService.getDictItems()
                if (response.isSuccessful && response.body() != null) {
                    val herbalList = response.body()?.data ?: emptyList()
                    if (herbalList.isEmpty()) {
                        _errorMessage.value = "Herbal not found"
                    } else {
                        _allHerbal.value = herbalList
                        _filteredHerbal.value = herbalList // Menampilkan artikel setelah diambil dari API
                    }
                } else {
                    _errorMessage.value = "Failed to load Herbal"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
