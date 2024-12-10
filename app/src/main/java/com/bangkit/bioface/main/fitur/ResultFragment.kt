package com.bangkit.bioface.main.fitur

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentResultBinding
import com.bangkit.bioface.main.adapter.HerbalSolutionAdapter
import com.bangkit.bioface.main.adapter.SkincareProductAdapter
import com.bangkit.bioface.network.response.HerbalSolution
import com.bangkit.bioface.network.response.PredictionResponse
import com.bangkit.bioface.network.response.SkincareProduct
import java.io.IOException

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private var capturedImage: Bitmap? = null
    private lateinit var predictionResponse: PredictionResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ScrollView {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the image and PredictionResponse from arguments
        val imageUri = arguments?.getParcelable<Uri>(ARG_IMAGE)
        capturedImage = try {
            if (imageUri != null) {
                // Convert Uri to Bitmap
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            } else {
                arguments?.getParcelable(ARG_IMAGE)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        predictionResponse = arguments?.getSerializable(ARG_PREDICTION_RESPONSE) as PredictionResponse

        // Display image in ImageView
        capturedImage?.let {
            binding.resultImageView.setImageBitmap(flipImage(it))
        }

        // Display processing results
        displayResults()

        // Set up RecyclerView for herbal solutions
        setupHerbalSolutionsRecyclerView(predictionResponse.recomendation?.herbalSolutions ?: emptyList())

        // Set up RecyclerView for skincare products
        setupSkincareProductsRecyclerView(predictionResponse.recomendation?.skincareProducts ?: emptyList())

        // Set up click listener for the finish button
        binding.finishButton.setOnClickListener {
            // Create an instance of the target fragment
            val targetFragment = ScanFragment()

            // Replace the current fragment with the target fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, targetFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun flipImage(image: Bitmap): Bitmap? {
        // Membuat matrix untuk membalik gambar secara horizontal
        val matrix = Matrix()
        matrix.preScale(-1f, 1f) // Membalik gambar secara horizontal

        // Menggunakan matrix untuk membuat bitmap baru
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, false)
    }

    private fun displayResults() {
        binding.conditionTextView.text = "Face Condition:\n${predictionResponse.faceDisease ?: "No data found"}"
        binding.diseaseDescriptionTextView.text = "Description:\n${predictionResponse.diseaseDescription ?: "No description"}"
        binding.diseaseAccuracyTextView.text = "Accuration:\n${predictionResponse.diseaseAccuracy ?: "No accuracy"}"

        // Menampilkan causes
        val causes = predictionResponse.predictionDetail?.causes
        binding.causesTextView.text = if (causes.isNullOrEmpty()) {
            "No Causes"
        } else {
            "Causes:\n${causes.joinToString(", ")}"
        }

        // Menampilkan detail disease accuracy
        val detailAccuracy = predictionResponse.predictionDetail?.detailDiseaseAccuracy
        binding.predictionDetailTextView.text = if (detailAccuracy.isNullOrEmpty()) {
            "No accuracy details"
        } else {
            val accuracyList = detailAccuracy.map { "${it.key}: ${it.value}" }
            "Prediction Detail:\n${accuracyList.joinToString(", ")}"
        }
    }

    private fun setupHerbalSolutionsRecyclerView(solutions: List<HerbalSolution>) {
        val adapter = HerbalSolutionAdapter(solutions)
        binding.herbalSolutionsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.herbalSolutionsRecyclerView.adapter = adapter
        binding.herbalSolutionsRecyclerView.isNestedScrollingEnabled = false // Menghindari konflik dengan ScrollView
    }

    private fun setupSkincareProductsRecyclerView(products: List<SkincareProduct>) {
        val adapter = SkincareProductAdapter(products)
        binding.skincareProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.skincareProductsRecyclerView.adapter = adapter
        binding.skincareProductsRecyclerView.isNestedScrollingEnabled = false // Menghindari konflik dengan ScrollView
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"
        private const val ARG_PREDICTION_RESPONSE = "arg_prediction_response"

        fun newInstance(image: Uri?, predictionResponse: PredictionResponse): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            args.putSerializable(ARG_PREDICTION_RESPONSE, predictionResponse)
            fragment.arguments = args
            return fragment
        }
    }
}