package com.mesutemre.kutuphanesistemi.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.holders.KitapYorumHolder
import com.mesutemre.kutuphanesistemi.model.KitapYorumModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YorumListeModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapYorumListeAdapter(private val mContext: Context,
                             private val kitapIslemService: IKitapIslemService,
                             private val pd: Dialog,
                             private val kitapId:Int,
                             private val yorumListe:ArrayList<KitapYorumModel>):
    RecyclerView.Adapter<KitapYorumHolder>(){

    private var YORUM_LISTE_URL = ProjectUtil.API_URL+"api/kitap/yorumlar";

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
        Glide
            .with(mContext)
            .load(yorum!!.kullaniciResim!!)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(holder.yorumKisiImageView);
        if(yorum.olusturan.username.equals(ProjectUtil.getStringDataFromSharedPreferences(mContext,ProjectUtil.SHARED_PREF_FILE,"kullaniciAdi"))){
            holder.yorumMenuImage.visibility = View.VISIBLE;
            holder.yorumMenuImage.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    holder.yorumMenuImage.setOnClickListener {
                        val popUp = PopupMenu(v!!.rootView.context,v!!);
                        popUp.menuInflater.inflate(R.menu.yorum_menu,popUp.menu);
                        popUp.setOnMenuItemClickListener {item ->
                            when(item.itemId){
                                R.id.action_yorumSil ->{
                                    val ad = AlertDialog.Builder(it.rootView.context);
                                    ad.setMessage(mContext.resources.getString(R.string.yorumSilmekİstiyor));
                                    ad.setPositiveButton(mContext.resources.getString(R.string.evet)){ dialogInterface, i ->
                                        yorumSil(yorum.id)
                                    }

                                    ad.setNegativeButton(mContext.resources.getString(R.string.hayir)){dialogInterface, i ->
                                    }

                                    ad.create().show();
                                    true;
                                }
                                else-> false;
                            }
                        }

                        popUp.show();
                    }
                }
            });

        }else{
            holder.yorumMenuImage.visibility = View.GONE;
        }
    }

    private fun yorumSil(id: Int?) {
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("status",0);
        jsonObj.put("id",id);

        kitapIslemService.yorumKaydet(jsonObj.toString()).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(mContext, mContext!!.resources.getString(R.string.yorumGuncellemeHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(mContext, mContext.resources.getString(R.string.yorumSilmeBasarili), "{fa-check}");
                    loadProgressPuanYorumListe();
                }
            }
        });
    }

    private fun loadProgressPuanYorumListe(){
        YORUM_LISTE_URL = YORUM_LISTE_URL+"/"+kitapId;
        pd.show();
        kitapIslemService.getYorumListe(YORUM_LISTE_URL).enqueue(object:
            Callback<YorumListeModel> {
            override fun onFailure(call: Call<YorumListeModel>?, t: Throwable) {
                pd.dismiss();
                SimpleToast.error(mContext, mContext.resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<YorumListeModel>?, response: Response<YorumListeModel>) {
                val yorumModel: YorumListeModel =  response.body() as YorumListeModel;
                yorumListe.clear();
                yorumListe.addAll(yorumModel.yorumListe);
                notifyDataSetChanged();
                pd.dismiss();
            }
        });
    }
}