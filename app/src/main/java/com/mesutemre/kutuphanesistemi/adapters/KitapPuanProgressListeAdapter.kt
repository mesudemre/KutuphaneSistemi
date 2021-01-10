package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapPuanProgressHolder
import com.mesutemre.kutuphanesistemi.model.KitapPuanModel

class KitapPuanProgressListeAdapter(private val mContext: Context, private val kitapPuanListe:ArrayList<KitapPuanModel>):
    RecyclerView.Adapter<KitapPuanProgressHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapPuanProgressHolder {
        val kitapPuanProgressCardView: View = LayoutInflater.from(mContext).inflate(R.layout.card_puan_progres_layout,parent,false);
        return KitapPuanProgressHolder(kitapPuanProgressCardView);
    }

    override fun getItemCount(): Int {
        return kitapPuanListe.size;
    }

    override fun onBindViewHolder(holder: KitapPuanProgressHolder, position: Int) {
        val puan:KitapPuanModel = kitapPuanListe.get(position);
        holder.cardPuanText.text = ""+puan.puan;
        holder.cardPuanProgress.progress = (puan.adet!!)*5;
    }
}