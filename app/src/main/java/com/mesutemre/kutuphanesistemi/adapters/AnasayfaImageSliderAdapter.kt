package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapHolder
import com.mesutemre.kutuphanesistemi.holders.SliderAdapterVH
import com.mesutemre.kutuphanesistemi.model.ImageSliderModel
import com.smarteist.autoimageslider.SliderViewAdapter

class AnasayfaImageSliderAdapter(private val mContext: Context, private val imageListe:ArrayList<ImageSliderModel>):
    SliderViewAdapter<SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val imageSliderView: View = LayoutInflater.from(mContext).inflate(R.layout.image_slider_layout,parent,false);
        return SliderAdapterVH(imageSliderView);
    }

    override fun getCount(): Int {
        return imageListe.size;
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val item:ImageSliderModel = imageListe.get(position);
        viewHolder.slideImageview.setImageResource(item.imageResId);
        viewHolder.slideImageTitle.text = item.imageTitle;
    }


}