package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.bangkit.bioface.R

class BannerAdapter(private val banners: List<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context

        // Inflate layout containing CardView and ImageView
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_banner, container, false)

        val cardView: CardView = view.findViewById(R.id.cardView)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // Set the image resource to ImageView
        imageView.setImageResource(banners[position])

        // Add the inflated view to the container (ViewPager)
        container.addView(view)

        // Return the view
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return banners.size
    }
}
