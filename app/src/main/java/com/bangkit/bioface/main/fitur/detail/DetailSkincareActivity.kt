package com.bangkit.bioface.main.fitur.detail

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
import com.bangkit.bioface.network.response.SkincareItem
import com.bangkit.bioface.viewmodel.DetailSkincareViewModel
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class DetailSkincareActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailSkincareViewModel

    private lateinit var imageSkincare: ImageView
    private lateinit var nameSkincare: TextView
    private lateinit var priceSkincare: TextView
    private lateinit var benefitSkincare: TextView
    private lateinit var buySkincare: Button
    private lateinit var progressBar: ProgressBar

    companion object {
        const val ARG_SKINCARE_ID = "skincare_id"

        fun newInstance(skincareId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(ARG_SKINCARE_ID, skincareId)
            return bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_skincare)

        imageSkincare = findViewById(R.id.skincareImage)
        nameSkincare = findViewById(R.id.skincareTitle)
        priceSkincare = findViewById(R.id.skincarePrice)
        benefitSkincare = findViewById(R.id.skincareBenefit)
        buySkincare = findViewById(R.id.btnBuySkincare)
        progressBar = findViewById(R.id.progressBar)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(DetailSkincareViewModel::class.java)



        val skincareId = intent.extras?.getInt(ARG_SKINCARE_ID, -1) ?: -1

        // Observasi skincare
        viewModel.skincare.observe(this) { skincare ->
            if (skincare != null) {
                updateUI(skincare)  // Memperbarui UI dengan skincare yang diterima
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
        if (skincareId != -1) {
            viewModel.getSkincareById(skincareId)
        } else {
            Toast.makeText(this, "Invalid Skincare Product ID", Toast.LENGTH_SHORT).show()
        }

        // Tombol beli skincare
        buySkincare.setOnClickListener {
            val skincare = viewModel.skincare.value

            skincare?.let { item ->
                val url = item.linkToBuy
                if (url != null && url.isNotEmpty()) {
                    // Membuka URL di browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No purchase URL available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(skincare: SkincareItem) {
        val priceFormatted = "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(skincare.price)
        priceSkincare.text = priceFormatted

        nameSkincare.text = skincare.name
        priceSkincare.text = priceFormatted
        benefitSkincare.text = skincare.description

        Glide.with(this)
            .load(skincare.image)
            .into(imageSkincare)
    }
}
