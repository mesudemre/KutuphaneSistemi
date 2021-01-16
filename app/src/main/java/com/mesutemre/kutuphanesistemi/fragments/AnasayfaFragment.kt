package com.mesutemre.kutuphanesistemi.fragments

import android.app.Dialog
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.DashKitapAdapter
import com.mesutemre.kutuphanesistemi.adapters.DashKitapturAdapter
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.model.KitapTurIstatistikModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_anasayfa.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnasayfaFragment:Fragment() {

    private lateinit var anasayfaView:View;
    private lateinit var sonEklenenKitaplar:RecyclerView;
    private lateinit var pd: Dialog;
    private lateinit var kitapService: IKitapIslemService;
    private lateinit var kullaniciService:IKullaniciService;
    private lateinit var dashKitapTurlerRw:RecyclerView;
    private lateinit var kitapTurIstatistik:PieChart;
    private lateinit var pieEntry:ArrayList<PieEntry>;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        anasayfaView = inflater.inflate(R.layout.fragment_anasayfa,container,false);
        initComponents();

        return anasayfaView;
    }

    private fun initComponents() {
        this.sonEklenenKitaplar = this.anasayfaView.findViewById(R.id.sonEklenenKitaplar) as RecyclerView;
        kitapService            = WebApiUtil.getKitapService(context!!);
        kullaniciService        = WebApiUtil.getKullaniciService();
        this.dashKitapTurlerRw  = this.anasayfaView.findViewById(R.id.dashKitapTurlerRw) as RecyclerView;
        this.kitapTurIstatistik = this.anasayfaView.findViewById(R.id.pieChart) as PieChart;

        activity!!.hizliErisimFabButton.visibility = View.VISIBLE;

        val sonKitaplarLayout = LinearLayoutManager(context!!,LinearLayoutManager.HORIZONTAL, false);
        sonEklenenKitaplar.setHasFixedSize(true);
        sonEklenenKitaplar.layoutManager = sonKitaplarLayout;

        val dashKitapturlerLayout = LinearLayoutManager(context!!,LinearLayoutManager.HORIZONTAL, false);
        dashKitapTurlerRw.setHasFixedSize(true);
        dashKitapTurlerRw.layoutManager = dashKitapturlerLayout;

        pd = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        this.loadSon5Kitap();
        this.loadKitapTurleri();
        this.loadKitapTurIstatistikler();
    }

    private fun loadKitapTurleri(){
        pd.show();
        kullaniciService.getKitapturListe().enqueue(object:
            Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(activity!!.applicationContext, resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                val ilgiAlanlariListe = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                val adapter = DashKitapturAdapter(activity!!.applicationContext,ilgiAlanlariListe);
                dashKitapTurlerRw.adapter = adapter;
                pd.dismiss();
            }
        });
    }

    private fun loadSon5Kitap(){
        pd.show();
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("minKayitNum",0);
        jsonObj.put("maxKayitNum",5);
        kitapService.getKitapListe(jsonObj.toString()).enqueue(object:
            Callback<ArrayList<KitapModel>> {
            override fun onFailure(call: Call<ArrayList<KitapModel>>?, t: Throwable) {
                Log.d("Exception",""+t.localizedMessage);
                SimpleToast.error(context, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<KitapModel>>?, response: Response<ArrayList<KitapModel>>) {
                val liste = response.body() as ArrayList<KitapModel>;
                val kitapAdapter:DashKitapAdapter = DashKitapAdapter(context!!,liste);
                sonEklenenKitaplar.adapter = kitapAdapter;
                pd.dismiss();
            }
        });
    }

    private  fun loadKitapTurIstatistikler(){
        kitapService.getKitapTurIstatistikListe().enqueue(object:
            Callback<ArrayList<KitapTurIstatistikModel>> {
            override fun onFailure(call: Call<ArrayList<KitapTurIstatistikModel>>?, t: Throwable) {
                Log.d("Exception",""+t.localizedMessage);
                SimpleToast.error(context, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<KitapTurIstatistikModel>>?, response: Response<ArrayList<KitapTurIstatistikModel>>) {
                val liste = response.body() as ArrayList<KitapTurIstatistikModel>;
                pieEntry = ArrayList<PieEntry>();
                val colorListe = mutableListOf<Int>()
                for (l in liste){
                    pieEntry.add(PieEntry(l.adet!!.toFloat(),l.aciklama));
                    colorListe.add(ProjectUtil.generateRandomColorCode());
                }
                val dataSet = PieDataSet(pieEntry, "");
                val data = PieData(dataSet);
                dataSet.colors = colorListe;
                pieChart.data = data;
                pieChart.description.text = "";
                pieChart.isDrawHoleEnabled = false;
                pieChart.invalidate();
                data.setValueTextSize(13f);

                pd.dismiss();
            }
        });
    }
}