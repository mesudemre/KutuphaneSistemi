package com.mesutemre.kutuphanesistemi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.listeners.TextInputErrorClearListener
import com.mesutemre.kutuphanesistemi.model.AccountCredentials
import com.mesutemre.kutuphanesistemi.model.KullaniciModel
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        val pd: Dialog = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        cirLoginButton.setOnClickListener {
            val kullaniciAdi:String = editTextKullaniciAdi.text.toString().trim();
            val sifre:String = editTextSifre.text.toString().trim();

            if(TextUtils.isEmpty(kullaniciAdi)){
                textInputKullaniciAdi.error = resources.getString(R.string.bosKullaniciAdiHata);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(kullaniciAdi)) {
                textInputSifre.error= resources.getString(R.string.bosSifreHata);
                return@setOnClickListener;
            }

            pd.show();
            val kullaniciService:IKullaniciService = WebApiUtil.getKullaniciService();
            val user = AccountCredentials(kullaniciAdi,sifre);

            kullaniciService.login(user).enqueue(object:Callback<String>{
                override fun onFailure(call: Call<String>?, t: Throwable) {
                    SimpleToast.error(applicationContext, t.message, "{fa-times-circle}");
                    pd.dismiss();
                }

                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    val token:String = response?.body().toString();
                    if(token != null && !token.contains("hatali")){
                        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"token",token);
                        getKullaniciBilgi(applicationContext,pd,kullaniciService,token);
                        //getKullaniciResim(applicationContext,pd);
                    }else{
                        SimpleToast.error(applicationContext, resources.getString(R.string.hataliLogin), "{fa-times-circle}");
                        pd.dismiss();
                    }
                }
            });
        }

        textInputKullaniciAdi.editText!!.addTextChangedListener(TextInputErrorClearListener(textInputKullaniciAdi));
        textInputSifre.editText!!.addTextChangedListener(TextInputErrorClearListener(textInputSifre));

        registerTextView.setOnClickListener {
            ProjectUtil.activityYonlendir(this,RegisterActivity());
        }

    }

    private fun getKullaniciBilgi( c: Context,pd: Dialog,kullaniciService: IKullaniciService,token:String) {
        kullaniciService.getKullaniciBilgi("Bearer "+token.trim()).enqueue(object:Callback<KullaniciModel>{
            override fun onFailure(call: Call<KullaniciModel>?, t: Throwable?) {
                Log.e("FAIL",t?.message);
                pd.dismiss();
            }

            override fun onResponse(call: Call<KullaniciModel>?, response: Response<KullaniciModel>) {
                val kullanici: KullaniciModel = response.body() as KullaniciModel;
                writeToSPKullaniciBilgileri(kullanici,applicationContext);
                ProjectUtil.activityYonlendir(c,MainActivity());
                pd.dismiss();
            }
        });
    }

    private fun writeToSPKullaniciBilgileri(kullanici: KullaniciModel, applicationContext: Context) {
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"adSoyad",kullanici.ad+" "+kullanici.soyad);
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"eposta",kullanici.eposta);
        val sb:StringBuilder = StringBuilder();
        for(rol in kullanici.roller){
            sb.append(rol.rol.value).append(",");
        }
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"rol",sb.toString());
        val sdf:SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy");
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"dogumTarih",sdf.format(kullanici.dogumTarihi));
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"kullaniciAdi",kullanici.username);
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"ad",kullanici.ad);
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"soyad",kullanici.soyad);
        val ia:StringBuilder = StringBuilder();
        for(i in kullanici.ilgiAlanlari){
            ia.append(i.id).append(",");
        }
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"ilgialanlar",ia.toString());
        ProjectUtil.putBooleanDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"haberdarmi",kullanici.haberdarmi);
        ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"cinsiyet",kullanici.cinsiyet.value)
    }

    override fun onBackPressed() {
        super.onBackPressed();
        ProjectUtil.anaEkranaDon(this);
    }
}