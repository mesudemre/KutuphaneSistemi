package com.mesutemre.kutuphanesistemi.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.YayineviListeAdapter
import com.mesutemre.kutuphanesistemi.model.YayinEviModel
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YayineviListeFragment(): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_yayinevi_liste,container,false);
        val mContext: Context = activity!!.applicationContext;
        val pd: Dialog = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);
        pd.show();

        val yayinEviListeRw:RecyclerView = view.findViewById(R.id.yayinEviListeId) as RecyclerView;
        yayinEviListeRw.setHasFixedSize(true);
        yayinEviListeRw.layoutManager = LinearLayoutManager(mContext);

        val parametreService: IParametreService = WebApiUtil.getParametreService(mContext);
        getYayinEviListe(parametreService,yayinEviListeRw,mContext,pd);

        return view;
    }

    private fun getYayinEviListe(parametreService: IParametreService,yayinEviListeRw:RecyclerView,c:Context,pd: Dialog){
        parametreService.getYayinEviListe().enqueue(object: Callback<ArrayList<YayinEviModel>> {
            override fun onFailure(call: Call<ArrayList<YayinEviModel>>?, t: Throwable) {
                SimpleToast.error(c, resources.getString(R.string.yayinEviListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<YayinEviModel>>?, response: Response<ArrayList<YayinEviModel>>) {
                val liste = response.body() as ArrayList<YayinEviModel>;
                yayinEviListeRw.adapter = YayineviListeAdapter(c,liste,pd);
                pd.dismiss();
            }
        });
    }

}