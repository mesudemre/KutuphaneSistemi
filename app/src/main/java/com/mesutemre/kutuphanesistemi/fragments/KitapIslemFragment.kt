package com.mesutemre.kutuphanesistemi.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.KitapEklemeActivity
import com.mesutemre.kutuphanesistemi.LoginActivity
import com.mesutemre.kutuphanesistemi.MainActivity
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.KitapListeAdapter
import com.mesutemre.kutuphanesistemi.customcomponents.LinearSpacingDecoration
import com.mesutemre.kutuphanesistemi.listeners.EndlessRecyclerViewScrollListener
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitapIslemFragment():Fragment() {

    private lateinit var kitapIslemView:View;
    private lateinit var kitapListeRw:RecyclerView;
    private lateinit var pd:Dialog;
    private lateinit var kitapService:IKitapIslemService;
    private var liste:ArrayList<KitapModel> = ArrayList<KitapModel>();
    private lateinit var kitapAdapter:KitapListeAdapter;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kitapIslemView = inflater.inflate(R.layout.fragment_kitap_islem,container,false);
        initComponents();
        initKitapListe();
        val scrollListener:EndlessRecyclerViewScrollListener;
        scrollListener = object:EndlessRecyclerViewScrollListener(4,kitapListeRw.layoutManager as LinearLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    pd.show();
                    val jsonObj: JSONObject = JSONObject();
                    jsonObj.put("minKayitNum",((page*4)));
                    jsonObj.put("maxKayitNum",4);
                    kitapService.getKitapListe(jsonObj.toString()).enqueue(object:
                        Callback<ArrayList<KitapModel>> {
                        override fun onFailure(call: Call<ArrayList<KitapModel>>?, t: Throwable) {
                            Log.d("Exception",""+t.localizedMessage);
                            SimpleToast.error(context, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
                            pd.dismiss();
                        }

                        override fun onResponse(call: Call<ArrayList<KitapModel>>?, response: Response<ArrayList<KitapModel>>) {
                            if(response.body() != null){
                                val yeniListe = response.body() as ArrayList<KitapModel>;
                                liste.addAll(yeniListe);
                                kitapAdapter.notifyItemInserted(liste.size);
                                val handler: Handler = Handler(Looper.getMainLooper());
                                handler.postDelayed(object:Runnable{
                                    override fun run() {
                                        kitapAdapter.notifyDataSetChanged();
                                    }
                                },1000);
                            }
                            pd.dismiss();
                        }
                    });


            }
        }

        kitapListeRw.addOnScrollListener(scrollListener);

        return kitapIslemView;
    }



    private fun initComponents(){
        kitapListeRw    = kitapIslemView.findViewById(R.id.kitapListeRw) as RecyclerView;
        kitapService    = WebApiUtil.getKitapService(context!!);

        pd = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        kitapListeRw.setHasFixedSize(true);
        //kitapListeRw.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        kitapListeRw.layoutManager = LinearLayoutManager(context);
        kitapListeRw.addItemDecoration(LinearSpacingDecoration(itemSpacing = 20, edgeSpacing = 30))
        activity!!.kitapEkleBtn.visibility = View.VISIBLE;
        activity!!.kitapEkleBtn.setOnClickListener {
            ProjectUtil.activityYonlendir(activity!!, KitapEklemeActivity());
        }
    }

    private fun initKitapListe(){
        pd.show();
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("minKayitNum",0);
        jsonObj.put("maxKayitNum",4);
        kitapService.getKitapListe(jsonObj.toString()).enqueue(object:
            Callback<ArrayList<KitapModel>> {
            override fun onFailure(call: Call<ArrayList<KitapModel>>?, t: Throwable) {
                Log.d("Exception",""+t.localizedMessage);
                SimpleToast.error(context, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<KitapModel>>?, response: Response<ArrayList<KitapModel>>) {
                liste = response.body() as ArrayList<KitapModel>;
                kitapAdapter = KitapListeAdapter(context!!,liste);
                kitapListeRw.adapter = kitapAdapter;
                pd.dismiss();
            }
        });
    }

}