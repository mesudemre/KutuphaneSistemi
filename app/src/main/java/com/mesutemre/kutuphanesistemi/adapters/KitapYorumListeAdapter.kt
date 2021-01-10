package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapYorumHolder
import com.mesutemre.kutuphanesistemi.model.KitapYorumModel
import com.mesutemre.kutuphanesistemi.util.ProjectUtil

class KitapYorumListeAdapter(private val mContext: Context, private val yorumListe:ArrayList<KitapYorumModel>):
    RecyclerView.Adapter<KitapYorumHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapYorumHolder {
        val kitapYorumView:View = LayoutInflater.from(mContext).inflate(R.layout.card_yorum,parent,false);
        return KitapYorumHolder(kitapYorumView);
    }

    override fun getItemCount(): Int {
        return yorumListe.size;
    }

    override fun onBindViewHolder(holder: KitapYorumHolder, position: Int) {
        val yorum:KitapYorumModel = yorumListe.get(position);

        holder.yorumAciklama.text = yorum.yorum;
        holder.yorumTarih.text = ProjectUtil.formatDate(yorum.olusturmaTar!!,"dd.MM.yyyy");
        holder.yorumKisiAdSoyad.text = yorum.olusturan!!.ad+" "+yorum.olusturan!!.soyad;
        holder.yorumKisiImageView.setImageBitmap(yorum!!.kullaniciResim?.let {
            ProjectUtil.getBitmapResourceFromBase64(
                it
            )
        });
    }


}