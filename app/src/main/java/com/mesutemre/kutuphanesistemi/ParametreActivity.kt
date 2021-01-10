package com.mesutemre.kutuphanesistemi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.pierry.simpletoast.SimpleToast
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mesutemre.kutuphanesistemi.customcomponents.CurvedBottomNavigationView
import com.mesutemre.kutuphanesistemi.fragments.KitapturListeFragment
import com.mesutemre.kutuphanesistemi.fragments.YayineviListeFragment
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_parametre.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParametreActivity : AppCompatActivity() {

    private lateinit var tempFragment: Fragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametre);

        val actionbar = supportActionBar;
        actionbar!!.title = resources.getString(R.string.parametreActivityTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);

        val pd: Dialog = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        initNavBottomPreferences(parametreBottomNavigation);

        supportFragmentManager.beginTransaction()?.replace(R.id.parametreFragmentTutucu,
            YayineviListeFragment()
        )?.commit();

        parametreBottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            if(menuItem.itemId == R.id.yayinEviItem){
                tempFragment = YayineviListeFragment();
            }else if(menuItem.itemId == R.id.kitapTurItem){
                tempFragment = KitapturListeFragment();
            }
            supportFragmentManager.beginTransaction().replace(R.id.parametreFragmentTutucu,tempFragment)?.commit();
            true;
        }

        parametreEkleFloatingButton.setOnClickListener {
            openParametreEklemeDlg(this,pd);
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    private fun initNavBottomPreferences(navBottomMenu: CurvedBottomNavigationView){
        navBottomMenu.inflateMenu(R.menu.parametre_menu_nav_items);
        navBottomMenu.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED;
        navBottomMenu.menu.getItem(1).isVisible = false;
    }

    private fun openParametreEklemeDlg(c: Context, pd: Dialog) {
        val view:View = layoutInflater.inflate(R.layout.dialog_parametre_ekleme,null);
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle(R.string.paramEkleDlgTitle)
            .setView(view)
            .setPositiveButton(R.string.kaydetLabel, null)
            .setNegativeButton(R.string.iptalLabel, null)
            .show();
        val paramTipCombo: Spinner = view.findViewById(R.id.paramTypeSpinner) as Spinner;
        val paramAciklama: EditText = view.findViewById(R.id.paramText) as EditText;
        val paramTipArr = c.resources.getStringArray(R.array.parametreTipArr);
        val adapter = ArrayAdapter(alertDialogBuilder.context,android.R.layout.simple_list_item_1,paramTipArr);
        paramTipCombo.adapter = adapter;

        val kaydetButton: Button = alertDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE);
        kaydetButton.setOnClickListener {
            val selectedParamTip = paramTipCombo.selectedItem;
            val aciklama = paramAciklama.text.toString();

            if(selectedParamTip.equals(resources.getString(R.string.paramTipSeciniz))){
                SimpleToast.warning(c, resources.getString(R.string.parametreSecinizUyari), "{fa-exclamation-triangle}");
                return@setOnClickListener;
            }

            if(aciklama.isEmpty()){
                SimpleToast.warning(c, resources.getString(R.string.aciklamaBosUyari), "{fa-exclamation-triangle}");
                return@setOnClickListener;
            }

            val jsonObj: JSONObject = JSONObject();
            jsonObj.put("aciklama",aciklama);
            val parametreService: IParametreService = WebApiUtil.getParametreService(c);

            pd.show();

            if(selectedParamTip.equals(resources.getString(R.string.paramKitapTur))){
                kitapTurKaydet(parametreService,pd,jsonObj.toString());
                alertDialogBuilder.dismiss();
                pd.dismiss();
            }else if(selectedParamTip.equals(resources.getString(R.string.paramYayinevi))){
                yayinEviKaydet(parametreService,pd,jsonObj.toString());
                alertDialogBuilder.dismiss();
            }
        }

        val iptalButton: Button = alertDialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE);
        iptalButton.setOnClickListener {
            alertDialogBuilder.dismiss();
        }
    }

    private fun kitapTurKaydet(parametreService: IParametreService,pd:Dialog,jsonStr:String){
        parametreService.kitapTurKaydet(jsonStr).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapTurSilmeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(applicationContext, respModel.statusMessage, "{fa-check}");
                    supportFragmentManager.beginTransaction().replace(R.id.parametreFragmentTutucu,KitapturListeFragment())?.commit();
                    pd.dismiss();
                }
            }
        });
    }

    private fun yayinEviKaydet(parametreService: IParametreService,pd:Dialog,jsonStr:String){
        parametreService.yayinEviKaydet(jsonStr).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.yayinEviSilmeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(applicationContext, respModel.statusMessage, "{fa-check}");
                    pd.dismiss();
                    supportFragmentManager.beginTransaction().replace(R.id.parametreFragmentTutucu,YayineviListeFragment())?.commit();
                }
            }
        });
    }
}
