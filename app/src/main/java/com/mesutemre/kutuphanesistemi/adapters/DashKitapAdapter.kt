package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mesutemre.kutuphanesistemi.KitapDetayActivity
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.DashKitapHolder
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.util.ProjectUtil

class DashKitapAdapter(private val mContext: Context, private val kitapListe:ArrayList<KitapModel>):
    RecyclerView.Adapter<DashKitapHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashKitapHolder {
        val dashKitapCardView: View = LayoutInflater.from(mContext).inflate(R.layout.dash_card_kitap,parent,false);
        return DashKitapHolder(dashKitapCardView);
    }

    override fun getItemCount(): Int {
        return kitapListe.size;
    }

    override fun onBindViewHolder(holder: DashKitapHolder, position: Int) {
        val kitap:KitapModel = kitapListe.get(position);

        Glide.with(mContext).load(kitap.kitapResimPath).into(holder.dashKitapImage);
        holder.dashKitapImage.setOnClickListener {
            putKitapDetayIntoSharedPreferences(mContext,kitap);
            ProjectUtil.activityYonlendir(mContext, KitapDetayActivity());
        }
    }

    private fun putKitapDetayIntoSharedPreferences(mContext: Context, kitap: KitapModel) {
        kitap.id?.let {
            ProjectUtil.putIntDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapId",
                it
            )
        };
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapAd",kitap.kitapAd.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"yazarAd",kitap.yazarAd.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapAciklama",kitap.kitapAciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapTur",kitap.kitapTur!!.aciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapYayinevi",kitap.yayinEvi!!.aciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapResim",kitap.kitapResimPath.toString());
        kitap.alinmatarihi?.let { ProjectUtil.formatDate(it,"dd.MM.yyyy") }?.let {
            ProjectUtil.putStringDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapAlinmaTarih",
                it
            )
        };
        ProjectUtil.putDoubleDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapPuan",kitap.kitapPuan);
    }
}