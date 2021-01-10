package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.mesutemre.kutuphanesistemi.KitapDetayActivity
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.util.ProjectUtil

class KitapFiltreAdapter(context:Context,
                         @LayoutRes private val layoutRes:Int,
                         val kitapListe:ArrayList<KitapModel>):ArrayAdapter<KitapModel>(context,layoutRes,kitapListe){

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View;
        view = inflater.inflate(layoutRes, parent, false);

        val kitapImage:ImageView = view.findViewById(R.id.searchItemImageId) as ImageView;
        val kitapAdTw:TextView = view.findViewById(R.id.searchKitapAdItemId) as TextView;
        val yazarAdTw:TextView = view.findViewById(R.id.searchKitapYazarAdItemId) as TextView;
        val mainLayout:ConstraintLayout = view.findViewById(R.id.mainSearchLayout);
        val kitap:KitapModel = getItem(position) as KitapModel;

        Glide.with(context).load(kitap.kitapResimPath).into(kitapImage);
        if(kitap.kitapAd!!.length>24){
            kitapAdTw.text     = kitap.kitapAd!!.substring(0,23)+"...";
        }else{
            kitapAdTw.text     = kitap.kitapAd;
        }

        yazarAdTw.text = kitap.yazarAd;

        mainLayout.setOnClickListener {
            putKitapDetayIntoSharedPreferences(context,kitap);
            ProjectUtil.activityYonlendir(context,KitapDetayActivity());
        }

        return view;
    }

    override fun getItem(position: Int): KitapModel? {
        return kitapListe.get(position);
    }

    private fun putKitapDetayIntoSharedPreferences(mContext: Context, kitap: KitapModel) {
        kitap.id?.let {
            ProjectUtil.putIntDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapId",
                it
            )
        };
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"kitapAd",kitap.kitapAd.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"yazarAd",kitap.yazarAd.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"kitapAciklama",kitap.kitapAciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"kitapTur",kitap.kitapTur!!.aciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"kitapYayinevi",kitap.yayinEvi!!.aciklama.toString());
        ProjectUtil.putStringDataToSharedPreferences(mContext,
            ProjectUtil.SHARED_PREF_FILE,"kitapResim",kitap.kitapResimPath.toString());
        kitap.kayittarihi?.let { ProjectUtil.formatDate(it,"dd.MM.yyyy") }?.let {
            ProjectUtil.putStringDataToSharedPreferences(mContext, ProjectUtil.SHARED_PREF_FILE,"kitapAlinmaTarih",
                it
            )
        };
        ProjectUtil.putDoubleDataToSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kitapPuan",kitap.kitapPuan);
    }

}