package com.mesutemre.kutuphanesistemi.adapters

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import java.lang.StringBuilder

class IlgiAlanlariSpinnerAdapter(val context:Context,var ilgiAlanListe:ArrayList<IlgiAlanlariParametreModel>,var kisiIlgiAlanIdListe:ArrayList<Int>?,var selectedIlgiAlanIdListe:ArrayList<Int>):BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View;
        if (convertView == null) {
            view = inflater.inflate(R.layout.ilgi_alanlari_spinner, parent, false);
        } else {
            view = convertView;
        }

        val cb:CheckBox = view.findViewById(R.id.ilgiAlanCheckBox) as CheckBox;
        val ia:IlgiAlanlariParametreModel = getItem(position) as IlgiAlanlariParametreModel;
        val tw:TextView = view.findViewById(R.id.tvIlgiAlanCombo) as TextView;

        if(ia.id == 0){
            tw.visibility = View.VISIBLE;
            cb.visibility = View.GONE;
        }else{
            cb.visibility = View.VISIBLE;
            cb.setText(ia.aciklama);
            tw.visibility = View.GONE;
        }


        if(kisiIlgiAlanIdListe != null && kisiIlgiAlanIdListe!!.size>0){
            for (i in kisiIlgiAlanIdListe!!){
                if(controlIsListedeMevcut(i,ia)){
                    cb.isChecked = true;
                    selectedIlgiAlanIdListe.remove(ia.id);
                    selectedIlgiAlanIdListe.add(ia.id);
                }
            }
        }

        cb.setOnCheckedChangeListener { compoundButton, b ->
            if(b && ia.id>0){
                selectedIlgiAlanIdListe.add(ia.id);
            }else{
                selectedIlgiAlanIdListe.remove(ia.id);
                if(kisiIlgiAlanIdListe != null){
                    val res = kisiIlgiAlanIdListe!!.find { id -> id == ia.id};
                    if(res != null && res>0){
                        val ia: StringBuilder = StringBuilder();
                        kisiIlgiAlanIdListe!!.remove(res);
                        ProjectUtil.removeFromSharedPreferences(context,ProjectUtil.SHARED_PREF_FILE,"ilgialanlar");
                        for(i in kisiIlgiAlanIdListe!!){
                            ia.append(res).append(",");
                        }
                        ProjectUtil.putStringDataToSharedPreferences(context,ProjectUtil.SHARED_PREF_FILE,"ilgialanlar",ia.toString());
                    }
                }

            }
        }

        return view;
    }

    override fun getItem(position: Int): Any {
        return ilgiAlanListe.get(position);
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getCount(): Int {
        return ilgiAlanListe.size;
    }

    private fun controlIsListedeMevcut(id:Int,ia:IlgiAlanlariParametreModel):Boolean{
        if(ia.id == id){
            return true;
        }
        return false;
    }

}
