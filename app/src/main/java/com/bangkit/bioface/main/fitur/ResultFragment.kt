package com.bangkit.bioface.main.fitur

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bioface.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private var capturedImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil gambar dari argumen
        capturedImage = arguments?.getParcelable(ARG_IMAGE)

        // Tampilkan gambar di ImageView
        binding.resultImageView.setImageBitmap(capturedImage)

        // Tampilkan hasil pemrosesan
        displayResults()

        // Set up click listener untuk tombol selesai
        binding.finishButton.setOnClickListener {
            // Kembali ke ScanFragment atau aktivitas sebelumnya
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }
    }

    private fun displayResults() {
        // Simulasi hasil pemrosesan
        // Gantilah dengan hasil yang sebenarnya dari model AI
        val condition = "Kulit Berminyak" // Contoh kondisi wajah
        val recommendation = "Gunakan produk pembersih wajah yang ringan." // Contoh rekomendasi produk

        binding.conditionTextView.text = "Kondisi Wajah: $condition"
        binding.recommendationTextView.text = "Rekomendasi Produk: $recommendation"
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(image: Bitmap?): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }
}
