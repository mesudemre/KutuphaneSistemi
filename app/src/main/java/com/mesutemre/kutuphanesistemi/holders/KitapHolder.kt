package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R

class KitapHolder(view: View): RecyclerView.ViewHolder(view)  {

    var kitapImage:ImageView;
    var kitapAdText:TextView;
    var kitapTurText:TextView;
    var cardLayoutId:RelativeLayout;
    var kitapAciklamaText:TextView;

    init {
        this.kitapImage          = view.findViewById(R.id.kitapCardImageId) as ImageView;
        this.kitapAdText         = view.findViewById(R.id.kitapCardAdTextId) as TextView;
        this.kitapTurText        = view.findViewById(R.id.kitapTurTextId) as TextView;
        this.cardLayoutId        = view.findViewById(R.id.cardLayoutId) as RelativeLayout;
        this.kitapAciklamaText   = view.findViewById(R.id.kitapAciklamaTextId) as TextView;
    }
}