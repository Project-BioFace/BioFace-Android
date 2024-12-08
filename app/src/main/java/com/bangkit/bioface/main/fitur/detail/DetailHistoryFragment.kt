package com.bangkit.bioface.main.fitur.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bioface.databinding.FragmentDetailHistoryBinding
import com.bangkit.bioface.main.adapter.HerbalSolutionAdapter
import com.bangkit.bioface.main.adapter.SkincareProductAdapter
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.HerbalSolution
import com.bangkit.bioface.network.response.PredictionHistory
import com.bangkit.bioface.network.response.SkincareProduct
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DetailHistoryFragment : Fragment() {
    private lateinit var binding: FragmentDetailHistoryBinding
    private var predictionId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil ID dari argumen
        predictionId = arguments?.getInt(ARG_PREDICTION_ID)

        // Ambil detail berdasarkan ID
        predictionId?.let { fetchPredictionDetail(it) }
    }

    private fun fetchPredictionDetail(id: Int) {
        val apiService = ApiClient.apiService()

        // Mendapatkan token dari pengguna yang sedang login
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        lifecycleScope.launch {
                            try {
                                // Mengambil detail history berdasarkan ID
                                val response = apiService.getHistoryDetail("Bearer $it", id)
                                // Pastikan response memiliki status "success" dan ambil data dari predictions
                                if (response.status == "success") {
                                    displayPredictionDetail(response.predictions[0]) // Ambil predictions
                                } else {
                                    Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun displayPredictionDetail(prediction: PredictionHistory) {
        // Tampilkan detail di UI
        binding.diseaseTextView.text = "Kondisi Wajah: ${prediction.faceDisease}"
        binding.accuracyTextView.text = "Akurasi: ${prediction.diseaseAccuracy}"
        binding.descriptionTextView.text = prediction.diseaseDescription
        Glide.with(this).load(prediction.imageUrl).into(binding.historyImageView)

        // Set up RecyclerView untuk herbal solutions
        setupHerbalSolutionsRecyclerView(prediction.recomendation.herbalSolutions ?: emptyList())

        // Set up RecyclerView untuk skincare products
        setupSkincareProductsRecyclerView(prediction.recomendation.skincareProducts ?: emptyList())

        // Set up click listener untuk tombol kembali
        binding.finishButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }
    }

    private fun setupHerbalSolutionsRecyclerView(solutions: List<HerbalSolution>) {
        val adapter = HerbalSolutionAdapter(solutions)
        binding.herbalSolutionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.herbalSolutionsRecyclerView.adapter = adapter
    }

    private fun setupSkincareProductsRecyclerView(products: List<SkincareProduct>) {
        val adapter = SkincareProductAdapter(products)
        binding.skincareProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.skincareProductsRecyclerView.adapter = adapter
    }

    companion object {
        private const val ARG_PREDICTION_ID = "arg_prediction_id"

        fun newInstance(predictionId: Int): DetailHistoryFragment {
            val fragment = DetailHistoryFragment()
            val args = Bundle()
            args.putInt(ARG_PREDICTION_ID, predictionId)
            fragment.arguments = args
            return fragment
        }
    }
}



