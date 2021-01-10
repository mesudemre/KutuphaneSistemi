package com.mesutemre.kutuphanesistemi.adapters

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapturHolder
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapturListeAdapter(private val mContext: Context, private val kitapTurListe:ArrayList<IlgiAlanlariParametreModel>, private val pd: Dialog) :
    RecyclerView.Adapter<KitapturHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapturHolder {
        val kitapTurCardView: View = LayoutInflater.from(mContext).inflate(R.layout.card_kitaptur,parent,false);
        return KitapturHolder(kitapTurCardView);
    }

    override fun getItemCount(): Int {
        return kitapTurListe.size;
    }

    override fun onBindViewHolder(holder: KitapturHolder, position: Int) {
        val kitapTur:IlgiAlanlariParametreModel = kitapTurListe.get(position);

        holder.kitapTurAciklama.text    = kitapTur.aciklama;
        holder.kitapTurDurum.text       = mContext.resources.getString(R.string.durum)+" : Aktif";
        holder.kitapTurParametreOlusturan.text  = mContext.resources.getString(R.string.olusturan)+" : "+kitapTur.olusturan.username;

        holder.expandStatuKitapturImage.setOnClickListener {
            if(holder.expandStatuKitapturImage.drawable.constantState == mContext.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp)?.constantState){
                holder.kitapTurDetayLayout.visibility = View.VISIBLE;
                holder.expandStatuKitapturImage.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_up_blue));
            }else{
                holder.kitapTurDetayLayout.visibility = View.GONE;
                holder.expandStatuKitapturImage.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
            }
        }

        holder.kitapTurSilImage.setOnClickListener {it->
            val ad = AlertDialog.Builder(it.rootView.context);
            ad.setMessage(mContext.resources.getString(R.string.kitapTurSilmekIstiyormusunuz));
            ad.setPositiveButton(mContext.resources.getString(R.string.evet)){ dialogInterface, i ->
                pd.show();
                val jsonObj: JSONObject = JSONObject();

                jsonObj.put("id",kitapTur.id);
                jsonObj.put("durum","PASIF");
                jsonObj.put("aciklama",kitapTur.aciklama);

                val parametreService: IParametreService = WebApiUtil.getParametreService(it.rootView.context);
                parametreService.kitapTurKaydet(jsonObj.toString()).enqueue(object:
                    Callback<ResponseStatusModel> {
                    override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                        SimpleToast.error(mContext, mContext.resources.getString(R.string.kitapTurSilmeHata), "{fa-times-circle}");
                        pd.dismiss();
                    }

                    override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                        if(response.body() != null){
                            val respModel = response.body() as ResponseStatusModel;
                            SimpleToast.info(mContext, respModel.statusMessage, "{fa-check}");
                            holder.kitapTurDetayLayout.visibility = View.GONE;
                            loadData(parametreService);
                        }
                    }
                });
            }

            ad.setNegativeButton(mContext.resources.getString(R.string.hayir)){dialogInterface, i ->
            }

            ad.create().show();
        }
    }

    private fun loadData(parametreService: IParametreService):Unit{
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("durum","AKTIF");

        parametreService.getKitapturListe(jsonObj.toString()).enqueue(object: Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(mContext, mContext.resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
                pd.dismiss();
            }
            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                val liste = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                Log.d("100","100");
                kitapTurListe.clear();
                kitapTurListe.addAll(liste);
                notifyDataSetChanged();
                pd.dismiss();
            }
        });
    }

}