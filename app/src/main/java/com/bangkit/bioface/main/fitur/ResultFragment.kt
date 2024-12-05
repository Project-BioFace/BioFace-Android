package com.bangkit.bioface.main.fitur

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bioface.databinding.FragmentResultBinding
import com.bangkit.bioface.main.adapter.HerbalSolutionAdapter
import com.bangkit.bioface.main.adapter.SkincareProductAdapter
import com.bangkit.bioface.network.response.HerbalSolution
import com.bangkit.bioface.network.response.PredictionResponse
import com.bangkit.bioface.network.response.SkincareProduct

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private var capturedImage: Bitmap? = null
    private lateinit var predictionResponse: PredictionResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ScrollView {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil gambar dan PredictionResponse dari argumen
        capturedImage = arguments?.getParcelable(ARG_IMAGE)
        predictionResponse = arguments?.getSerializable(ARG_PREDICTION_RESPONSE) as PredictionResponse

        // Tampilkan gambar di ImageView
        binding.resultImageView.setImageBitmap(flipImage(capturedImage))

        // Tampilkan hasil pemrosesan
        displayResults()

        // Set up RecyclerView untuk herbal solutions
        setupHerbalSolutionsRecyclerView(predictionResponse.recomendation?.herbalSolutions ?: emptyList())

        // Set up RecyclerView untuk skincare products
        setupSkincareProductsRecyclerView(predictionResponse.recomendation?.skincareProducts ?: emptyList())

        // Set up click listener untuk tombol selesai
        binding.finishButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }
    }

    private fun flipImage(image: Bitmap?): Bitmap? {
        if (image == null) return null

        // Membuat matrix untuk membalik gambar secara horizontal
        val matrix = Matrix()
        matrix.preScale(-1f, 1f) // Membalik gambar secara horizontal

        // Menggunakan matrix untuk membuat bitmap baru
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, false)
    }

    private fun displayResults() {
        binding.conditionTextView.text = "Kondisi Wajah: ${predictionResponse.faceDisease ?: "Tidak ada data"}"
        binding.diseaseDescriptionTextView.text = predictionResponse.diseaseDescription ?: "Tidak ada deskripsi"
        binding.diseaseAccuracyTextView.text = "Akurasi: ${predictionResponse.diseaseAccuracy ?: "Tidak ada akurasi"}"
        // Menampilkan causes
        val causes = predictionResponse.predictionDetail?.causes
        binding.causesTextView.text = if (causes.isNullOrEmpty()) {
            "Tidak ada penyebab"
        } else {
            "Penyebab: ${causes.joinToString(", ")}"
        }

        // Menampilkan detail disease accuracy
        val detailAccuracy = predictionResponse.predictionDetail?.detailDiseaseAccuracy
        binding.predictionDetailTextView.text = if (detailAccuracy.isNullOrEmpty()) {
            "Tidak ada detail akurasi"
        } else {
            val accuracyList = detailAccuracy.map { "${it.key}: ${it.value}" }
            "Detail Akurasi: ${accuracyList.joinToString(", ")}"
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
        private const val ARG_IMAGE = "arg_image"
        private const val ARG_PREDICTION_RESPONSE = "arg_prediction_response"

        fun newInstance(image: Bitmap?, predictionResponse: PredictionResponse): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            args.putSerializable(ARG_PREDICTION_RESPONSE, predictionResponse)
            fragment.arguments = args
            return fragment
        }
    }
}
