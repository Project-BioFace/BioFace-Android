package com.bangkit.bioface.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.ResponseDictDetail
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailHerbalViewModel : ViewModel() {

    // LiveData untuk menyimpan skincare yang diterima
    private val _herbal = MutableLiveData<DictItem?>()
    val herbal : LiveData<DictItem?> get() = _herbal

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk error message
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Fungsi untuk mendapatkan skincare berdasarkan ID
    fun getHerbalById(id: Int) {
        _isLoading.value = true // Menandakan bahwa data sedang dimuat
        viewModelScope.launch {
            try {
                // Memanggil API untuk mendapatkan skincare berdasarkan ID
                val response: Response<ResponseDictDetail> = ApiClient.apiService2().getDictItemById(id)

                // Mengecek apakah response berhasil dan body tidak null
                if (response.isSuccessful && response.body() != null) {
                    val skincare = response.body()?.data // Ambil data skincare

                    // Jika skincare tidak null, set skincare ke LiveData
                    if (herbal != null) {
                        _herbal.value = skincare
                    } else {
                        _error.value = "Herbal data is null"
                    }

                    _isLoading.value = false // Mengubah status loading menjadi false
                    Log.d("DetailHerbalViewModel", "Herbal data received: $herbal")
                } else {
                    // Jika response tidak berhasil, beri pesan error
                    _error.value = "Failed to load Detail Herbal"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Menangani kesalahan jika ada
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
                Log.e("DetailHerbalViewModel", "Error: ${e.message}")
            }
        }
    }
}
