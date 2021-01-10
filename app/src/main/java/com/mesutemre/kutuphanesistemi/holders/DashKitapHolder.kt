package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R

class DashKitapHolder(view: View): RecyclerView.ViewHolder(view) {

    var dashKitapImage: ImageView;

    init {
        this.dashKitapImage     = view.findViewById(R.id.dashKitapCardImageId) as ImageView;
    }
}