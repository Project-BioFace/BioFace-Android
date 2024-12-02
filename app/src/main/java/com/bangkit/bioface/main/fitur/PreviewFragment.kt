package com.bangkit.bioface.main.fitur

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentPreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewFragment : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private var capturedImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil gambar dari argumen
        capturedImage = arguments?.getParcelable(ARG_IMAGE)

        // Tampilkan gambar di ImageView
        binding.imageView.setImageBitmap(capturedImage)

        // Set up click listeners
        binding.retakeButton.setOnClickListener {
            // Kembali ke ScanFragment
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }

        binding.scanButton.setOnClickListener {
            // Proses gambar menggunakan model AI
            processImage(capturedImage)
        }
    }

    private fun processImage(image: Bitmap?) {
        if (image != null) {
            // Tampilkan ProgressBar dan TextView
            binding.progressBar.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            binding.progressText.text = "Memproses gambar..."

            // Simulasi pemrosesan gambar
            lifecycleScope.launch {
                // Simulasi proses 0-100%
                for (i in 0..100 step 10) {
                    withContext(Dispatchers.Main) {
                        binding.progressText.text = "Memproses... $i%"
                    }
                    delay(500) // Simulasi waktu pemrosesan
                }

                // Panggil model AI untuk memproses gambar
                // Misalnya, Anda bisa memanggil fungsi dari rekan Anda di sini
                // Contoh:
                // val result = aiModel.processImage(image)

                // Setelah pemrosesan selesai, navigasi ke ResultFragment
                navigateToResultFragment(image) // Ganti dengan hasil pemrosesan
            }
        } else {
            Toast.makeText(requireContext(), "Gambar tidak tersedia untuk diproses", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToResultFragment(image: Bitmap?) {
        val resultFragment = ResultFragment.newInstance(image) // Ganti dengan hasil pemrosesan
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, resultFragment) // Ganti dengan ID container yang sesuai
            .addToBackStack(null) // Tambahkan ke back stack jika ingin kembali
            .commit()
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(image: Bitmap?): PreviewFragment {
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }
}
