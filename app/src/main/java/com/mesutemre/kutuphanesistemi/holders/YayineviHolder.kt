package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R

class YayineviHolder(view:View): RecyclerView.ViewHolder(view) {

    var yayinEviAciklama:TextView;
    var expandStatuImage:ImageView;
    var yayinEviDetayLayout:ConstraintLayout;
    var yayinEviDurum:TextView;
    var parametreOlusturan:TextView;
    var yayinEviSilImage:ImageView;

    init {
        this.yayinEviAciklama       = view.findViewById(R.id.yayinEviAciklamaTextView) as TextView;
        this.expandStatuImage       = view.findViewById(R.id.expandStatuYayinImageView) as ImageView;
        this.yayinEviDetayLayout    = view.findViewById(R.id.yayinEviDetayLayout) as ConstraintLayout;
        this.yayinEviDurum          = view.findViewById(R.id.yayinEviDurumTextView) as TextView;
        this.parametreOlusturan     = view.findViewById(R.id.parametreOlusturanTextView) as TextView;
        this.yayinEviSilImage       = view.findViewById(R.id.yayinEviSilImage) as ImageView;
    }
}