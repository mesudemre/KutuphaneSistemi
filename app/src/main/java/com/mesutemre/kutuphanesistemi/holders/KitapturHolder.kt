package com.mesutemre.kutuphanesistemi.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R

class KitapturHolder(view: View): RecyclerView.ViewHolder(view) {

    var kitapTurAciklama: TextView;
    var expandStatuKitapturImage: ImageView;
    var kitapTurDetayLayout: ConstraintLayout;
    var kitapTurDurum: TextView;
    var kitapTurParametreOlusturan: TextView;
    var kitapTurSilImage: ImageView;

    init {
        this.kitapTurAciklama               = view.findViewById(R.id.kitapTurAciklamaTextView) as TextView;
        this.expandStatuKitapturImage       = view.findViewById(R.id.expandStatuKitapturImageView) as ImageView;
        this.kitapTurDetayLayout            = view.findViewById(R.id.kitapTurDetayLayout) as ConstraintLayout;
        this.kitapTurDurum                  = view.findViewById(R.id.kitapTurDurumTextView) as TextView;
        this.kitapTurParametreOlusturan     = view.findViewById(R.id.kitapTurParametreOlusturanTextView) as TextView;
        this.kitapTurSilImage               = view.findViewById(R.id.kitapTurSilImage) as ImageView;
    }
}