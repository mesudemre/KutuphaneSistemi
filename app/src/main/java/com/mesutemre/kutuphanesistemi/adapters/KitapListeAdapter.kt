package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mesutemre.kutuphanesistemi.KitapDetayActivity
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapHolder
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import java.net.URL

class KitapListeAdapter(private val mContext: Context, private val kitapListe:ArrayList<KitapModel>):
    RecyclerView.Adapter<KitapHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapHolder {
        val kitapTurCardView: View = LayoutInflater.from(mContext).inflate(R.layout.card_kitap,parent,false);
        return KitapHolder(kitapTurCardView);
    }

    override fun getItemCount(): Int {
        return kitapListe.size;
    }

    override fun onBindViewHolder(holder: KitapHolder, position: Int) {
        val kitap:KitapModel = kitapListe.get(position);

        Glide.with(mContext).load(kitap.kitapResimPath).into(holder.kitapImage);
        if(kitap.kitapAd!!.length>55){
            holder.kitapAdText.text     = kitap.kitapAd!!.substring(0,54)+"...";
        }else{
            holder.kitapAdText.text     = kitap.kitapAd;

        }
        holder.kitapTurText.text    = "Kitap Türü : "+kitap.kitapTur?.aciklama;

        if(kitap.kitapAciklama!!.length>110){
            holder.kitapAciklamaText.text = kitap.kitapAciklama?.substring(0,110)+"...";
        }else{
            holder.kitapAciklamaText.text = kitap.kitapAciklama;
        }


        holder.cardLayoutId.setOnClickListener {
            putKitapDetayIntoSharedPreferences(mContext,kitap);
            ProjectUtil.activityYonlendir(mContext,KitapDetayActivity());
        }
    }

    fun addAllItems(yeniListe:ArrayList<KitapModel>):Unit{
        this.kitapListe.clear();
        this.kitapListe.addAll(yeniListe);
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