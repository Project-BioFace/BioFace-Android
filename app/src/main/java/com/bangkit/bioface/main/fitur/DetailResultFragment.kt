package com.bangkit.bioface.main.fitur

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentDetailResultBinding
import com.bangkit.bioface.main.adapter.ResultItem
import java.util.Locale

class DetailResultFragment : Fragment() {
    private lateinit var binding: FragmentDetailResultBinding
    private lateinit var scanResult: ResultItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data dari argumen
        scanResult = arguments?.getParcelable(ARG_SCAN_RESULT)!!

        // Tampilkan data
        binding.detailImageView.setImageBitmap(scanResult.image)
        binding.detailConditionTextView.text = scanResult.condition
        binding.detailRecommendationTextView.text = scanResult.recommendation
        binding.detailTimestampTextView.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(scanResult.timestamp))
    }

    companion object {
        private const val ARG_SCAN_RESULT = "arg_scan_result"

        fun newInstance(scanResult: ResultItem): DetailResultFragment {
            val fragment = DetailResultFragment()
            val args = Bundle()
            args.putParcelable(ARG_SCAN_RESULT, scanResult)
            fragment.arguments = args
            return fragment
        }
    }
}