package com.bangkit.bioface.main.fitur.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.viewmodel.DetailHerbalViewModel
import com.bumptech.glide.Glide

class DetailHerbalActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailHerbalViewModel

    private lateinit var imageHerbal: ImageView
    private lateinit var nameHerbal: TextView
    private lateinit var benefitHerbal: TextView
    private lateinit var howToUseHerbal: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val ARG_HERBAL_ID = "herbal_id"

        fun newInstance(herbalId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(ARG_HERBAL_ID, herbalId)
            return bundle
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_herbal)

        imageHerbal = findViewById(R.id.herbalImage)
        nameHerbal = findViewById(R.id.herbalTitle)
        benefitHerbal = findViewById(R.id.herbalBenefit)
        howToUseHerbal = findViewById(R.id.herbalUse)
        progressBar = findViewById(R.id.progressBar)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(DetailHerbalViewModel::class.java)


        val herbalId = intent.extras?.getInt(ARG_HERBAL_ID, -1) ?: -1

        // Observasi skincare
        viewModel.herbal.observe(this) { herbal ->
            if (herbal != null) {
                updateUI(herbal)  // Memperbarui UI dengan skincare yang diterima
            }
        }

        // Observasi loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi error
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Ambil skincare berdasarkan ID
        if (herbalId != -1) {
            viewModel.getHerbalById(herbalId)
        } else {
            Toast.makeText(this, "Invalid Skincare Product ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(herbal: DictItem) {
        nameHerbal.text = herbal.name
        benefitHerbal.text = "Benefit:\n${herbal.benefit}"
        howToUseHerbal.text = "How to use:\n${herbal.howToUse} "

        Glide.with(this)
            .load(herbal.image)
            .into(imageHerbal)
    }
}
