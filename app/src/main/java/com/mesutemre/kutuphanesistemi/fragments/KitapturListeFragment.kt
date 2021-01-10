package com.mesutemre.kutuphanesistemi.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.KitapturListeAdapter
import com.mesutemre.kutuphanesistemi.adapters.YayineviListeAdapter
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.YayinEviModel
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapturListeFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_kitaptur_liste,container,false);
        val mContext: Context = activity!!.applicationContext;
        val pd: Dialog = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);
        pd.show();

        val kitapTurListeRw:RecyclerView = view.findViewById(R.id.kitapTurListeId);
        kitapTurListeRw.setHasFixedSize(true);
        kitapTurListeRw.layoutManager = LinearLayoutManager(mContext);

        val parametreService: IParametreService = WebApiUtil.getParametreService(mContext);
        getKitapTurListe(parametreService,kitapTurListeRw,mContext,pd);

        return view;
    }

    private fun getKitapTurListe(parametreService: IParametreService,kitapTurListeRw:RecyclerView,c:Context,pd: Dialog) {
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("durum","AKTIF");
        parametreService.getKitapturListe(jsonObj.toString()).enqueue(object: Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(c, resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                val liste = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                kitapTurListeRw.adapter = KitapturListeAdapter(c,liste,pd);
                pd.dismiss();
            }
        });
    }
}