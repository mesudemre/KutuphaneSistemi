package com.mesutemre.kutuphanesistemi

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.adapters.IlgiAlanlariSpinnerAdapter
import com.mesutemre.kutuphanesistemi.listeners.TextInputErrorClearListener
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity() {

    private lateinit var ilgiAlanlariListe:ArrayList<IlgiAlanlariParametreModel>;
    private var selectedIlgiAlanlar:ArrayList<Int> = ArrayList<Int>();
    private lateinit var selectedImageUri: Uri;
    companion object {
        const val CAMERA_REQUEST_CODE = 12345;
        const val GALERI_REQUEST_CODE = 54321;
    }
    private lateinit var pd: Dialog;
    private lateinit var kullaniciService:IKullaniciService;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register);

        initializeComponents();

    }

    private fun initializeComponents() {
        pd = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        kullaniciService = WebApiUtil.getKullaniciService();

        initDatePicker();
        fillIlgiAlanlariListe();

        registerProfilKullaniciAd.editText!!.addTextChangedListener(TextInputErrorClearListener(registerProfilKullaniciAd));
        registerSifreTextInput.editText!!.addTextChangedListener(TextInputErrorClearListener(registerSifreTextInput));
        registerProfilAdText.editText!!.addTextChangedListener(TextInputErrorClearListener(registerProfilAdText));
        registerProfilSoyadText.editText!!.addTextChangedListener(TextInputErrorClearListener(registerProfilSoyadText));
        registerProfilDogumTarih.editText!!.addTextChangedListener(TextInputErrorClearListener(registerProfilDogumTarih));
        registerProfilEposta.editText!!.addTextChangedListener(TextInputErrorClearListener(registerProfilEposta));

        profilKaydetButton.setOnClickListener {
            val kullaniciAdi:String = registerProfilKullaniciAd.editText!!.text.toString();
            val sifre:String        = registerSifreTextInput.editText!!.text.toString();
            val ad:String           = registerProfilAdText.editText!!.text.toString();
            val soyad:String        = registerProfilSoyadText.editText!!.text.toString();
            val dogumTar:String     = registerProfilDogumTarih.editText!!.text.toString();
            val cinsiyet:Int        = registerCinsiyetRadioGroup.checkedRadioButtonId;
            val eposta:String       = registerProfilEposta.editText!!.text.toString();
            val haberdarMi:Boolean  = registerHaberdarCheckBox.isChecked;

            if(TextUtils.isEmpty(kullaniciAdi)){
                registerProfilKullaniciAd.error = resources.getString(R.string.registerKullaniciAdiHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(sifre)){
                registerSifreTextInput.error = resources.getString(R.string.registerSifreHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(ad)){
                registerProfilAdText.error = resources.getString(R.string.registerAdHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(soyad)){
                registerProfilSoyadText.error = resources.getString(R.string.registerSoyadHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(eposta)){
                registerProfilEposta.error = resources.getString(R.string.epostaHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(dogumTar)){
                registerProfilDogumTarih.error = resources.getString(R.string.registerDogumtarihHata);
                return@setOnClickListener;
            }

            if(cinsiyet == -1){
                SimpleToast.warning(applicationContext, resources.getString(R.string.cinsiyetHata), "{fa-exclamation-triangle}");
                return@setOnClickListener;
            }

            if(this.selectedIlgiAlanlar == null || this.selectedIlgiAlanlar.isEmpty()){
                SimpleToast.warning(applicationContext, resources.getString(R.string.ilgiAlanHata), "{fa-exclamation-triangle}");
                return@setOnClickListener;
            }

            val cal:Calendar = Calendar.getInstance();
            val dtarArr = dogumTar.split(".");
            cal.set(Calendar.YEAR,dtarArr[2].toInt());
            cal.set(Calendar.MONTH,dtarArr[1].toInt()-1);
            cal.set(Calendar.DAY_OF_MONTH,dtarArr[0].toInt());

            val jsonObj: JSONObject = JSONObject();
            val iaArr: JSONArray = JSONArray();
            var iaJsonObj: JSONObject = JSONObject();

            jsonObj.put("username",kullaniciAdi);
            jsonObj.put("password",sifre);
            jsonObj.put("ad",ad)
            jsonObj.put("soyad",soyad);
            jsonObj.put("dogumTarihi", ProjectUtil.formatDate(cal.time,"yyyy-MM-dd"));
            jsonObj.put("eposta",eposta);
            jsonObj.put("haberdarmi",haberdarMi);

            if(cinsiyet == R.id.erkekRadioButton){
                jsonObj.put("cinsiyet","E");
            }else{
                jsonObj.put("cinsiyet","K");
            }

            for(s in selectedIlgiAlanlar){
                iaJsonObj.put("id",s);
                iaArr.put(iaJsonObj);
                iaJsonObj = JSONObject();
            }

            jsonObj.put("ilgiAlanlari",iaArr);

            pd.show();

            Log.d("JSON",jsonObj.toString());

            kullaniciService.kullaniciKaydet(jsonObj.toString()).enqueue(object:
                Callback<ResponseStatusModel> {
                override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                    SimpleToast.error(applicationContext, resources.getString(R.string.kullaniciKayitHata), "{fa-times-circle}");
                    pd.dismiss();
                }

                override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                    if(response.body() != null){
                        val respModel = response.body() as ResponseStatusModel;
                        SimpleToast.info(applicationContext, resources.getString(R.string.kullaniciKayitBasarili), "{fa-check}");
                        pd.dismiss();
                        ProjectUtil.activityYonlendir(applicationContext,LoginActivity());
                    }
                }
            });
        }
    }

    private fun fillIlgiAlanlariListe() {
        pd.show();
        kullaniciService.getKitapturListe().enqueue(object:
            Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                ilgiAlanlariListe = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                ilgiAlanlariListe.add(IlgiAlanlariParametreModel(0,"",ilgiAlanlariListe.get(0).durum,ilgiAlanlariListe.get(0).olusturan));
                val ilgiAlanlariListe2 = ilgiAlanlariListe.sortedWith(compareBy({it.id}));
                ilgiAlanlariListe.clear();
                for(i in ilgiAlanlariListe2){
                    ilgiAlanlariListe.add(i);
                }
                val adapter = IlgiAlanlariSpinnerAdapter(application,ilgiAlanlariListe,null,selectedIlgiAlanlar);
                registerIlgiAlanlariSpinner.adapter = adapter;
                pd.dismiss();
            }
        });
    }

    private fun initDatePicker() {
        val cal: Calendar = Calendar.getInstance();
        val yil: Int = cal.get(Calendar.YEAR);
        val ay: Int = cal.get(Calendar.MONTH);
        val gun: Int = cal.get(Calendar.DAY_OF_MONTH);

        registerProfilDogumTarih.editText!!.setOnClickListener { it: View ->
            var datePicker = DatePickerDialog(it.context, R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { datePicker, y, a, g ->
                    registerProfilDogumTarih.editText!!.setText("$g.${a + 1}.$y");
                }, yil, ay - 1, gun
            );

            datePicker.setTitle(resources.getString(R.string.selectTarih));
            datePicker.setButton(
                DialogInterface.BUTTON_POSITIVE,
                resources.getString(R.string.ayarlaLabel),
                datePicker
            );
            datePicker.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                resources.getString(R.string.iptalLabel),
                datePicker
            );

            datePicker.show();
        }
    }
}
