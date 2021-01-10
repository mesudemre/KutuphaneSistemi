package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R

class KitapPuanProgressHolder(view: View): RecyclerView.ViewHolder(view)  {

    var cardPuanText:TextView;
    var cardPuanProgress:ProgressBar;

    init {
        this.cardPuanText       = view.findViewById(R.id.cardPuanTextView) as TextView;
        this.cardPuanProgress   = view.findViewById(R.id.cardPuanProgressBar) as ProgressBar;
    }
}