package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.mesutemre.kutuphanesistemi.R
import com.mikhaellopez.circularimageview.CircularImageView

class KitapYorumHolder(view: View): RecyclerView.ViewHolder(view) {

    var yorumKisiImageView:CircularImageView;
    var yorumKisiAdSoyad:MaterialTextView;
    var yorumTarih:MaterialTextView;
    var yorumMenuImage:ImageView;
    var yorumAciklama:MaterialTextView;

    init {
        this.yorumKisiImageView = view.findViewById(R.id.yorumKisiImageViewId) as CircularImageView;
        this.yorumKisiAdSoyad   = view.findViewById(R.id.yorumKisiAdSoyadTextView) as MaterialTextView;
        this.yorumTarih         = view.findViewById(R.id.yorumTarihTextView) as MaterialTextView;
        this.yorumMenuImage     = view.findViewById(R.id.yorumMenuImageId) as ImageView;
        this.yorumAciklama      = view.findViewById(R.id.yorumAciklamaTextView) as MaterialTextView;
    }
}