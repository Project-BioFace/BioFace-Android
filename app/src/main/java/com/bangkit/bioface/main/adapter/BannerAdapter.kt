package com.bangkit.bioface.main.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

class BannerAdapter(private val banners: List<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageView.setImageResource(banners[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        // Set width to create spacing effect
        val layoutParams = ViewGroup.LayoutParams(
            (container.width * 0.8).toInt(), // 80% of the container width
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = layoutParams

        container.addView(imageView)
        return imageView
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

