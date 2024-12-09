package com.bangkit.bioface.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.PredictionHistory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryViewModel : ViewModel() {
    private val _historyList = MutableLiveData<List<PredictionHistory>>()
    val historyList: LiveData<List<PredictionHistory>> get() = _historyList

    fun fetchHistory() {
        viewModelScope.launch {
            try {
                val apiService = ApiClient.apiService1()
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                token?.let {
                    val response = apiService.getHistory("Bearer $it")
                    if (response.status == "Success") {
                        _historyList.value = response.predictions // Set data ke LiveData
                    } else {
                        _historyList.value = emptyList() // Set ke list kosong jika tidak ada data
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _historyList.value = emptyList() // Set ke list kosong jika terjadi kesalahan
            }
        }
    }





    // Menghapus history berdasarkan ID
    fun deleteHistory(predictionId: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val apiService = ApiClient.apiService1()

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        viewModelScope.launch {
                            try {
                                val response = apiService.deletePredictionHistory("Bearer $it", predictionId)
                                if (response.status == "success") {
                                    onSuccess() // Panggil callback sukses
                                } else {
                                    onFailure("Gagal menghapus history") // Panggil callback gagal
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onFailure("Terjadi kesalahan: ${e.message}") // Panggil callback gagal
                            }
                        }
                    }
                } else {
                    onFailure("Gagal mendapatkan token")
                }
            }
    }

    // Menghapus semua history
    fun deleteAllHistory(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val apiService = ApiClient.apiService1()

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        viewModelScope.launch {
                            try {
                                val response = apiService.deleteAllHistory("Bearer $it")
                                if (response.status == "success") {
                                    onSuccess() // Panggil callback sukses
                                } else {
                                    onFailure("Gagal menghapus semua history") // Panggil callback gagal
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onFailure("Terjadi kesalahan: ${e.message}") // Panggil callback gagal
                            }
                        }
                    }
                } else {
                    onFailure("Gagal mendapatkan token")
                }
            }
    }
}