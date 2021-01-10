package com.mesutemre.kutuphanesistemi.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.YayineviHolder
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YayinEviModel
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YayineviListeAdapter(private val mContext: Context,private val yayinEviListe:ArrayList<YayinEviModel>,private val pd: Dialog):RecyclerView.Adapter<YayineviHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YayineviHolder {
        val yayinEviCardView:View = LayoutInflater.from(mContext).inflate(R.layout.card_yayinevi,parent,false);
        return YayineviHolder(yayinEviCardView);
    }

    override fun getItemCount(): Int {
        return yayinEviListe.size;
    }

    override fun onBindViewHolder(holder: YayineviHolder, position: Int) {
        val yayinEvi:YayinEviModel = yayinEviListe.get(position);

        holder.yayinEviAciklama.text    = yayinEvi.aciklama;
        holder.yayinEviDurum.text       = mContext.resources.getString(R.string.durum)+" : Aktif";
        holder.parametreOlusturan.text  = mContext.resources.getString(R.string.olusturan)+" : "+yayinEvi.olusturan?.username;

        holder.yayinEviSilImage.setOnClickListener {it->
            val ad = AlertDialog.Builder(it.rootView.context);
            ad.setMessage(mContext.resources.getString(R.string.yayinEviSilmekIstiyormusunuz));
            ad.setPositiveButton(mContext.resources.getString(R.string.evet)){ dialogInterface, i ->
                pd.show();
                val jsonObj:JSONObject = JSONObject();

                jsonObj.put("id",yayinEvi.id);
                jsonObj.put("durum","PASIF");
                jsonObj.put("aciklama",yayinEvi.aciklama);

                val parametreService: IParametreService = WebApiUtil.getParametreService(it.rootView.context);
                parametreService.yayinEviKaydet(jsonObj.toString()).enqueue(object:
                    Callback<ResponseStatusModel> {
                    override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                        SimpleToast.error(mContext, mContext.resources.getString(R.string.yayinEviSilmeHata), "{fa-times-circle}");
                        pd.dismiss();
                    }

                    override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                        if(response.body() != null){
                            val respModel = response.body() as ResponseStatusModel;
                            SimpleToast.info(mContext, respModel.statusMessage, "{fa-check}");
                            holder.yayinEviDetayLayout.visibility = View.GONE;
                            loadData(parametreService);
                        }
                    }
                });
            }

            ad.setNegativeButton(mContext.resources.getString(R.string.hayir)){dialogInterface, i ->
            }

            ad.create().show();
        }

        holder.expandStatuImage.setOnClickListener {
            if(holder.expandStatuImage.drawable.constantState == mContext.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp)?.constantState){
                holder.yayinEviDetayLayout.visibility = View.VISIBLE;
                holder.expandStatuImage.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_up_blue));
            }else{
                holder.yayinEviDetayLayout.visibility = View.GONE;
                holder.expandStatuImage.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
            }
        }
    }

    private fun loadData(parametreService: IParametreService):Unit{
        parametreService.getYayinEviListe().enqueue(object: Callback<ArrayList<YayinEviModel>> {
            override fun onFailure(call: Call<ArrayList<YayinEviModel>>?, t: Throwable) {
                SimpleToast.error(mContext, mContext.resources.getString(R.string.yayinEviListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<YayinEviModel>>?, response: Response<ArrayList<YayinEviModel>>) {
                val liste = response.body() as ArrayList<YayinEviModel>;
                yayinEviListe.clear();
                yayinEviListe.addAll(liste);
                notifyDataSetChanged();
                pd.dismiss();
            }
        });
    }
}