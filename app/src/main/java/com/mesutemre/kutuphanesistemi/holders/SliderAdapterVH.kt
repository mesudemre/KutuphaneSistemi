package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mesutemre.kutuphanesistemi.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapterVH(view: View): SliderViewAdapter.ViewHolder(view) {

    var slideImageview: ImageView;
    var slideImageTitle: TextView;

    init {
        this.slideImageview     = view.findViewById(R.id.iv_auto_image_slider) as ImageView;
        this.slideImageTitle    = view.findViewById(R.id.tv_auto_image_slider) as TextView;
    }
}