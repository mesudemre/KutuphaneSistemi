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
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.AnasayfaImageSliderAdapter
import com.mesutemre.kutuphanesistemi.adapters.DashKitapAdapter
import com.mesutemre.kutuphanesistemi.model.ImageSliderModel
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnasayfaFragment:Fragment() {

    private lateinit var anasayfaView:View;
    private lateinit var sonEklenenKitaplar:RecyclerView;
    private lateinit var imageSlider:SliderView;
    private lateinit var pd: Dialog;
    private lateinit var kitapService: IKitapIslemService;

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
        this.imageSlider        = this.anasayfaView.findViewById(R.id.imageSlider) as SliderView;
        kitapService            = WebApiUtil.getKitapService(context!!);

        activity!!.hizliErisimFabButton.visibility = View.VISIBLE;

        val sonKitaplarLayout = LinearLayoutManager(context!!,LinearLayoutManager.HORIZONTAL, false);
        sonEklenenKitaplar.setHasFixedSize(true);
        sonEklenenKitaplar.layoutManager = sonKitaplarLayout;

        pd = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        this.loadImageSlide();
        this.loadSon5Kitap();
    }

    private fun loadImageSlide(){
        var imageListe:ArrayList<ImageSliderModel> = ArrayList<ImageSliderModel>();
        imageListe.add(ImageSliderModel(R.drawable.lib1,context!!.resources.getString(R.string.kutuphaneResimAciklama1)));
        imageListe.add(ImageSliderModel(R.drawable.lib2,context!!.resources.getString(R.string.kutuphaneResimAciklama2)));
        imageListe.add(ImageSliderModel(R.drawable.lib3,context!!.resources.getString(R.string.kutuphaneResimAciklama3)));
        imageSlider.setSliderAdapter(AnasayfaImageSliderAdapter(context!!,imageListe));
        imageSlider.startAutoCycle();
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
}