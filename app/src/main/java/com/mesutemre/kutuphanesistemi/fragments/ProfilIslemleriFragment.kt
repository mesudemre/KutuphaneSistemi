package com.mesutemre.kutuphanesistemi.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import com.github.pierry.simpletoast.SimpleToast
import com.google.android.material.textfield.TextInputLayout
import com.mesutemre.kutuphanesistemi.LoginActivity
import com.mesutemre.kutuphanesistemi.R
import com.mesutemre.kutuphanesistemi.adapters.IlgiAlanlariSpinnerAdapter
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.KullaniciResimModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProfilIslemleriFragment:Fragment() {

    private lateinit var profilImage:ImageView;
    private var profilImageChanged:Boolean = false;
    private lateinit var ilgiAlanlariListe:ArrayList<IlgiAlanlariParametreModel>;
    private var selectedIlgiAlanlar:ArrayList<Int> = ArrayList<Int>();
    private var kisiIlgiAlanlar:ArrayList<Int> = ArrayList<Int>();
    private lateinit var selectedImageUri:Uri;
    companion object {
        const val CAMERA_REQUEST_CODE = 12345;
        const val GALERI_REQUEST_CODE = 54321;
    }

    private lateinit var kullaniciAdSoyadTw:TextView;
    private lateinit var kullaniciAd:TextInputLayout;
    private lateinit var kullaniciProfilAd:TextInputLayout;
    private lateinit var kullaniciProfilSoyad:TextInputLayout;
    private lateinit var kullaniciEposta:TextInputLayout;
    private lateinit var dogumTarih:TextInputLayout;
    private lateinit var ilgiAlanlariSpinner:Spinner;
    private lateinit var haberdarCheckBox:CheckBox;
    private lateinit var kaydetButton:Button;
    private lateinit var cinsiyetRg:RadioGroup;
    private lateinit var pd: Dialog;

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_profil_islemler,container,false);
        pd = Dialog(activity!!);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        pd.show();

        initializeComponents(view);

        kaydetButton.setOnClickListener {
            val ad = AlertDialog.Builder(it.rootView.context);
            ad.setMessage(context!!.resources.getString(R.string.kullaniciGuncellemeUyari));

            ad.setPositiveButton(context!!.resources.getString(R.string.evet)){ dialogInterface, i ->
                pd.show();

                val dTar: String? = dogumTarih.editText!!.text.toString();
                val gun:Int = Integer.parseInt(dTar!!.split(".").get(0));
                val ay:Int = Integer.parseInt(dTar!!.split(".").get(1));
                val yil:Int = Integer.parseInt(dTar!!.split(".").get(2));

                var dogumTarDate:Date = Date();
                val cal:Calendar = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH,gun);
                cal.set(Calendar.MONTH,ay-1);
                cal.set(Calendar.YEAR,yil);
                dogumTarDate = cal.time;

                val jsonObj:JSONObject = JSONObject();
                val iaArr:JSONArray = JSONArray();
                var iaJsonObj:JSONObject = JSONObject();

                jsonObj.put("username",kullaniciAd!!.editText!!.text);
                jsonObj.put("ad",kullaniciProfilAd!!.editText!!.text)
                jsonObj.put("soyad",kullaniciProfilSoyad!!.editText!!.text);
                jsonObj.put("dogumTarihi",ProjectUtil.formatDate(dogumTarDate,"yyyy-MM-dd"));
                jsonObj.put("eposta",kullaniciEposta!!.editText!!.text);
                jsonObj.put("haberdarmi",haberdarCheckBox.isChecked);

                if(cinsiyetRg.checkedRadioButtonId == R.id.erkekRadioButton){
                    jsonObj.put("cinsiyet","E");
                }else{
                    jsonObj.put("cinsiyet","K");
                }

                if(selectedIlgiAlanlar != null && selectedIlgiAlanlar.size != 0){
                    for(s in selectedIlgiAlanlar){
                        iaJsonObj.put("id",s);
                        iaArr.put(iaJsonObj);
                        iaJsonObj = JSONObject();
                    }
                }else{
                    val kisiIlgiAlanlar = getKisiIlgiAlanlari(context!!);
                    for(s in kisiIlgiAlanlar){
                        iaJsonObj.put("id",s);
                        iaArr.put(iaJsonObj);
                        iaJsonObj = JSONObject();
                    }
                }

                jsonObj.put("ilgiAlanlari",iaArr);

                val kullaniciService: IKullaniciService = WebApiUtil.getKullaniciService(context!!);

                kullaniciService.kullaniciGuncelle(jsonObj.toString()).enqueue(object:
                    Callback<ResponseStatusModel> {
                    override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                        SimpleToast.error(activity, activity!!.resources.getString(R.string.kullaniciGuncellemeHata), "{fa-times-circle}");
                        pd.dismiss();
                    }

                    override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                        if(response.body() != null){
                            val respModel = response.body() as ResponseStatusModel;
                            SimpleToast.info(activity, respModel.statusMessage, "{fa-check}");
                            if(profilImageChanged){
                                kullaniciResimGuncelle(kullaniciAd!!.editText!!.text.toString(),kullaniciService);
                            }else{
                                pd.dismiss();
                                ProjectUtil.deleteAllSharedPreferencesData(context!!,ProjectUtil.SHARED_PREF_FILE);
                                ProjectUtil.activityYonlendir(context!!,LoginActivity());
                            }
                        }
                    }
                });
            }

            ad.setNegativeButton(context!!.resources.getString(R.string.hayir)){dialogInterface, i ->
            }

            ad.create().show();

        }

        profilImage.setOnClickListener {it:View->
            val menuBuilder:MenuBuilder = MenuBuilder(it.context);
            val menuInflater:MenuInflater = MenuInflater(it.context);
            menuInflater.inflate(R.menu.profil_image_degistir,menuBuilder);
            val optionsMenu:MenuPopupHelper = MenuPopupHelper(it.context,menuBuilder,it);
            optionsMenu.setForceShowIcon(true);

            menuBuilder.setCallback(object:MenuBuilder.Callback{
                override fun onMenuModeChange(menu: MenuBuilder) {
                }

                override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                    if(item.itemId == R.id.action_cam){
                        val intent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAMERA_REQUEST_CODE);
                    }else if(item.itemId == R.id.action_localStorage){
                        val intent:Intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent,""),GALERI_REQUEST_CODE);
                    }
                    return true;
                }

            })

            optionsMenu.show();
        }

        return view;
    }

    private fun initializeComponents(view:View) {
        profilImage             = view.findViewById(R.id.profilImage) as ImageView;
        kullaniciAdSoyadTw      = view.findViewById(R.id.profilAdSoyad) as TextView;
        kullaniciAd             = view.findViewById(R.id.profilKullaniciAd) as TextInputLayout;
        kullaniciProfilAd       = view.findViewById(R.id.profilAdText) as TextInputLayout;
        kullaniciProfilSoyad    = view.findViewById(R.id.profilSoyadText) as TextInputLayout;
        kullaniciEposta         = view.findViewById(R.id.profilEposta) as TextInputLayout;
        dogumTarih              = view.findViewById(R.id.profilDogumTarih) as TextInputLayout;
        ilgiAlanlariSpinner     = view.findViewById(R.id.ilgiAlanlariSpinner) as Spinner;
        haberdarCheckBox        = view.findViewById(R.id.haberdarCheckBox) as CheckBox;
        kaydetButton            = view.findViewById(R.id.profilGuncelleButton) as Button;
        cinsiyetRg              = view.findViewById(R.id.cinsiyetRadioGroup) as RadioGroup;

        getKullaniciResim(activity!!.applicationContext,pd,profilImage);
        kullaniciAdSoyadTw.text = ProjectUtil.getStringDataFromSharedPreferences(activity !!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"adSoyad");
        kullaniciProfilAd.editText!!.setText(ProjectUtil.getStringDataFromSharedPreferences(activity !!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"ad"));
        kullaniciProfilSoyad.editText!!.setText(ProjectUtil.getStringDataFromSharedPreferences(activity !!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"soyad"));
        kullaniciAd.editText!!.setText(ProjectUtil.getStringDataFromSharedPreferences(activity !!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"kullaniciAdi"));
        kullaniciEposta.editText!!.setText(ProjectUtil.getStringDataFromSharedPreferences(activity !!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"eposta"));
        dogumTarih.editText!!.setText(ProjectUtil.getStringDataFromSharedPreferences(activity!!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"dogumTarih"));

        val dTar: String? = ProjectUtil.getStringDataFromSharedPreferences(activity!!.applicationContext,ProjectUtil.SHARED_PREF_FILE,"dogumTarih");
        val gun:Int = Integer.parseInt(dTar!!.split(".").get(0));
        val ay:Int = Integer.parseInt(dTar!!.split(".").get(1));
        val yil:Int = Integer.parseInt(dTar!!.split(".").get(2));

        dogumTarih.editText!!.setOnClickListener {it:View->
            var datePicker = DatePickerDialog(it.context,R.style.DatePickerTheme,DatePickerDialog.OnDateSetListener { datePicker, y, a, g ->
                dogumTarih.editText!!.setText("$g.${a+1}.$y");
            },yil,ay-1,gun);

            if(ay<10){
                datePicker = DatePickerDialog(it.context,R.style.DatePickerTheme,DatePickerDialog.OnDateSetListener { datePicker, y, a, g ->
                    dogumTarih.editText!!.setText("$g.0${a+1}.$y");
                },yil,ay-1,gun);
            }


            datePicker.setTitle(resources.getString(R.string.selectTarih));
            datePicker.setButton(DialogInterface.BUTTON_POSITIVE,resources.getString(R.string.ayarlaLabel),datePicker);
            datePicker.setButton(DialogInterface.BUTTON_NEGATIVE,resources.getString(R.string.iptalLabel),datePicker);

            datePicker.show();

        }
        haberdarCheckBox.isChecked = ProjectUtil.getBooleanDataFromSharedPreferences(context!!,ProjectUtil.SHARED_PREF_FILE,"haberdarmi");

        val cinsiyet:String? = ProjectUtil.getStringDataFromSharedPreferences(context!!,ProjectUtil.SHARED_PREF_FILE,"cinsiyet");
        if(cinsiyet.equals("ERKEK")){
            cinsiyetRg.check(R.id.erkekRadioButton);
        }else{
            cinsiyetRg.check(R.id.kadinRadioButton);
        }
        fillIlgiAlanlariListe(ilgiAlanlariSpinner,activity!!.applicationContext);
    }

    private fun kullaniciResimGuncelle(kullaniciAd: String, kullaniciService: IKullaniciService) {
        val usernameParam:RequestBody = RequestBody.create(MediaType.parse("text/plain"),kullaniciAd);
        val originalFile: File =  org.apache.commons.io.FileUtils.getFile(ProjectUtil.getPath(context!!,selectedImageUri));
        val fileParam:RequestBody = RequestBody.create(MediaType.parse(context!!.contentResolver.getType(selectedImageUri)),originalFile);
        val file:MultipartBody.Part = MultipartBody.Part.createFormData("file",originalFile.name,fileParam);

        kullaniciService.kullaniciResimGuncelle(file,usernameParam).enqueue(object: Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(context!!, resources.getString(R.string.profilResimGuncellemeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(activity, respModel.statusMessage, "{fa-check}");
                    pd.dismiss();
                    ProjectUtil.deleteAllSharedPreferencesData(context!!,ProjectUtil.SHARED_PREF_FILE);
                    ProjectUtil.activityYonlendir(context!!,LoginActivity());
                }
            }
        });
    }

    private fun fillIlgiAlanlariListe(ilgiAlanlariSpinner: Spinner, applicationContext: Context?) {
        val parametreService: IParametreService = WebApiUtil.getParametreService(applicationContext!!);
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("durum","AKTIF");
        pd.show();

        parametreService.getKitapturListe(jsonObj.toString()).enqueue(object: Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                ilgiAlanlariListe = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                ilgiAlanlariListe.add(IlgiAlanlariParametreModel(0,"",
                    ilgiAlanlariListe.get(0).durum,ilgiAlanlariListe.get(0).olusturan,ilgiAlanlariListe.get(0).resim));
                kisiIlgiAlanlar = getKisiIlgiAlanlari(context!!);
                val ilgiAlanlariListe2 = ilgiAlanlariListe.sortedWith(compareBy({it.id}));
                ilgiAlanlariListe.clear();
                for(i in ilgiAlanlariListe2){
                    ilgiAlanlariListe.add(i);
                }
                val adapter = IlgiAlanlariSpinnerAdapter(context!!,ilgiAlanlariListe,kisiIlgiAlanlar,selectedIlgiAlanlar);
                ilgiAlanlariSpinner.adapter = adapter;
            }
        });
    }

    private fun getKisiIlgiAlanlari(context: Context): ArrayList<Int> {
        var liste:ArrayList<Int> = ArrayList<Int>();
        val ilgialanSp: String? = ProjectUtil.getStringDataFromSharedPreferences(context,ProjectUtil.SHARED_PREF_FILE,"ilgialanlar");
        val ilgialanListe = ilgialanSp?.split(",");
        if (ilgialanListe != null) {
            for(i in ilgialanListe){
                if(!i.isEmpty() && !i.isBlank()){
                    liste.add(Integer.parseInt(i));
                }
            }
        }

        return liste;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345 && resultCode == Activity.RESULT_OK) {
            val photo:Bitmap = data!!.getExtras()?.get("data") as Bitmap;
            profilImage.setImageBitmap(photo);
            this.selectedImageUri = ProjectUtil.getImageUriFromBitmap(context!!,photo);
            this.profilImageChanged = true;
        }else if(requestCode == 54321 && resultCode == Activity.RESULT_OK){
            this.selectedImageUri = data!!.data!!;
            val photo:Bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver,selectedImageUri);
            profilImage.setImageBitmap(photo);
            this.profilImageChanged = true;
        }
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
}