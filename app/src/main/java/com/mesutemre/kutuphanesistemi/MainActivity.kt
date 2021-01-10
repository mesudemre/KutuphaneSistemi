package com.mesutemre.kutuphanesistemi

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.adapters.KitapFiltreAdapter
import com.mesutemre.kutuphanesistemi.fragments.AnasayfaFragment
import com.mesutemre.kutuphanesistemi.fragments.KitapIslemFragment
import com.mesutemre.kutuphanesistemi.fragments.ProfilIslemleriFragment
import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.model.KullaniciResimModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var kullaniciAdSoyad:TextView;
    private lateinit var kullaniciResim:ImageView;
    private lateinit var tempFragment:Fragment;
    private lateinit var kitapService: IKitapIslemService;
    private lateinit var pd:Dialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        pd = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);
        pd.show();

        setSupportActionBar(toolbarNav);
        val toggle = ActionBarDrawerToggle(this,drawer, toolbarNav as Toolbar?,0,0);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        val navHeaderView:View =navigationView.getHeaderView(0);

        this.kullaniciAdSoyad = navHeaderView.findViewById(R.id.kullaniciAdSoyadTextView) as TextView;
        this.kullaniciResim = navHeaderView.findViewById(R.id.userImage) as ImageView;
        kitapService    = WebApiUtil.getKitapService(this);

        setNavigationHeaderValues(applicationContext,pd);
        setFABButtonMenuActions();
        if(ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"navfragment") != null
            &&ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"navfragment")
                .equals("KitapIslemFragment") ){
            ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"navfragment");
            tempFragment = KitapIslemFragment();
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentNavTutucu,tempFragment).commit();
        }else{
            hizliErisimFabButton.visibility = View.VISIBLE;
            kitapEkleBtn.visibility = View.GONE;
            tempFragment = AnasayfaFragment();
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentNavTutucu,tempFragment).commit();
        }
        navigationView.setNavigationItemSelectedListener {item ->
            pd.show();
            if(item.itemId == R.id.anasayfaItem){
                item.setCheckable(true);
                kitapEkleBtn.visibility = View.GONE;
                hizliErisimFabButton.visibility = View.VISIBLE;
                tempFragment = AnasayfaFragment();
            }else if(item.itemId == R.id.kitapIslemlerItem){
                hizliErisimFabButton.visibility = View.GONE;
                item.setCheckable(true);
                tempFragment = KitapIslemFragment();
            }else if(item.itemId == R.id.parametreIslemlerItem){
                kitapEkleBtn.visibility = View.GONE;
                hizliErisimFabButton.visibility = View.GONE;
                ProjectUtil.activityYonlendir(this,ParametreActivity());
            }else if(item.itemId == R.id.profilIslemlerItem){
                item.setCheckable(true);
                hizliErisimFabButton.visibility = View.GONE;
                kitapEkleBtn.visibility = View.GONE;
                tempFragment = ProfilIslemleriFragment();
            }else if(item.itemId == R.id.cikisItem){
                val ad = AlertDialog.Builder(navigationView.context);
                ad.setMessage(resources.getString(R.string.cikisYapmakIstiyormusunuz));
                ad.setPositiveButton(resources.getString(R.string.evet)){ dialogInterface, i ->
                    ProjectUtil.deleteAllSharedPreferencesData(applicationContext,ProjectUtil.SHARED_PREF_FILE);
                    ProjectUtil.activityYonlendir(applicationContext,LoginActivity());
                }

                ad.setNegativeButton(resources.getString(R.string.hayir)){dialogInterface, i ->
                }

                ad.create().show();

            }

            if(item.itemId != R.id.cikisItem){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentNavTutucu,tempFragment).commit();
            }

            pd.dismiss();
            drawer.closeDrawer(GravityCompat.START);
            true;
        }

        pd.dismiss();
    }

    private fun setFABButtonMenuActions() {
        kitapListeFabButton.setOnClickListener {
            hizliErisimFabButton.toggle(false);
            hizliErisimFabButton.visibility = View.GONE;
            tempFragment = KitapIslemFragment();
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentNavTutucu,tempFragment).commit();
        }

        kitapEklemeFabButton.setOnClickListener {
            hizliErisimFabButton.toggle(false);
            ProjectUtil.activityYonlendir(this, KitapEklemeActivity());
        }

        profilIslemFabButton.setOnClickListener {
            hizliErisimFabButton.toggle(false);
            hizliErisimFabButton.visibility = View.GONE;
            tempFragment = ProfilIslemleriFragment();
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentNavTutucu,tempFragment).commit();
        }
    }

    private fun checkPermissions() {
        var camPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(camPermission != PackageManager.PERMISSION_GRANTED){//İzin aktif edilmemişse
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),12345);
        }

        camPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(camPermission  != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),54321);
        }

        camPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET);
        if(camPermission  != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),100);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.kutuphane_toolbar_head_menu,menu);
        val searchItem:MenuItem = menu.findItem(R.id.app_bar_search);
        val searchView:SearchView = searchItem.actionView as SearchView;
        searchView.queryHint = resources.getString(R.string.araHeaderLabel);

        searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 2;

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(query?.length!! >2){
                    var liste:ArrayList<KitapModel> = ArrayList<KitapModel>();
                    val jsonObj: JSONObject = JSONObject();
                    jsonObj.put("kitapAd",query);
                    jsonObj.put("yazaarAd",query);
                    kitapService.getKitapListe(jsonObj.toString()).enqueue(object:
                        Callback<ArrayList<KitapModel>> {
                        override fun onFailure(call: Call<ArrayList<KitapModel>>?, t: Throwable) {
                            SimpleToast.error(applicationContext, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
                        }

                        override fun onResponse(call: Call<ArrayList<KitapModel>>?, response: Response<ArrayList<KitapModel>>) {
                            if(response.body() != null){
                                liste = response.body() as ArrayList<KitapModel>;
                                searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).setAdapter(KitapFiltreAdapter(applicationContext,R.layout.kitap_search_item_layout,liste));
                            }
                        }
                    });
                }

                return true;
            }
        })


        return true;
    }



    private fun setNavigationHeaderValues(c:Context,pd:Dialog){
        this.kullaniciAdSoyad.text = resources.getString(R.string.hosGeldiniz)+ProjectUtil.getStringDataFromSharedPreferences(c,ProjectUtil.SHARED_PREF_FILE,"adSoyad");
        this.getKullaniciResim(c,pd,this.kullaniciResim);
    }

    private fun getKullaniciResim(
        applicationContext: Context,
        pd: Dialog,
        kullaniciResim: ImageView
    ) {
        val kullaniciService: IKullaniciService = WebApiUtil.getKullaniciService(applicationContext);
        kullaniciService.getKullaniciResim().enqueue(object: Callback<KullaniciResimModel> {
            override fun onFailure(call: Call<KullaniciResimModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.resimGetirmeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<KullaniciResimModel>?, response: Response<KullaniciResimModel>) {
                val kullaniciResimModel: KullaniciResimModel = response.body() as KullaniciResimModel;
                kullaniciResim.setImageBitmap(ProjectUtil.getBitmapResourceFromBase64(kullaniciResimModel.userImageBase64));
                pd.dismiss();
            }
        });
    }

    override fun onBackPressed() {
        val ad = AlertDialog.Builder(navigationView.context);
        ad.setMessage(resources.getString(R.string.kapatmakIstiyormusunuz));
        ad.setPositiveButton(resources.getString(R.string.evet)){ dialogInterface, i ->
            finishAffinity();
        }

        ad.setNegativeButton(resources.getString(R.string.hayir)){dialogInterface, i ->
        }

        ad.create().show();
    }
}
