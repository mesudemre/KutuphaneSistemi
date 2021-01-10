package com.mesutemre.kutuphanesistemi

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.pierry.simpletoast.SimpleToast
import com.mesutemre.kutuphanesistemi.adapters.KitapPuanProgressListeAdapter
import com.mesutemre.kutuphanesistemi.adapters.KitapYorumListeAdapter
import com.mesutemre.kutuphanesistemi.customcomponents.LinearSpacingDecoration
import com.mesutemre.kutuphanesistemi.listeners.TextInputErrorClearListener
import com.mesutemre.kutuphanesistemi.model.KitapPuanModel
import com.mesutemre.kutuphanesistemi.model.KitapYorumModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YorumListeModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import kotlinx.android.synthetic.main.activity_kitap_detay.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class KitapDetayActivity : AppCompatActivity() {

    private lateinit var kitapIslemService: IKitapIslemService;
    private lateinit var puanListe:ArrayList<KitapPuanModel>;
    private lateinit var yorumListe:ArrayList<KitapYorumModel>;
    private var kitapId:Int = 0;
    private var YORUM_LISTE_URL = ProjectUtil.API_URL+"api/kitap/yorumlar";
    private lateinit var pd: Dialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_detay);

        val actionbar = supportActionBar;
        actionbar!!.title = resources.getString(R.string.kitapDetayActivityTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);

        this.kitapIslemService      = WebApiUtil.getKitapService(this);

        initComponents();
    }

    private fun initComponents() {
        kitapId = ProjectUtil.getIntDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapId");
        val imagePath: String? = ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapResim");
        val kitapAd:String?  = ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAd");
        val yazarAd:String?=  ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"yazarAd");
        val aciklama:String? =  ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAciklama");
        val yayinEvi:String?= ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapYayinevi");
        val alinmaTar:String?=ProjectUtil.getStringDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAlinmaTarih");
        val kitapPuan:Float?=ProjectUtil.getFloatDataFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapPuan");

        pd = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);

        Glide.with(this).load(imagePath).into(kitapDetayImageId);
        kitapDetayAciklamaTextId.text = aciklama;
        kitapDetayAdTextId.text = kitapAd;
        kitapDetayYazarAdTextId.text = yazarAd;
        kitapDetayYayineviTextId.text = yayinEvi;
        kitapDetayAlinmaTarTextId.text = alinmaTar;

        YORUM_LISTE_URL = YORUM_LISTE_URL+"/$kitapId"

        kitapDetayAciklamaTextId.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                kitapDetayAciklamaTextId.viewTreeObserver.removeOnPreDrawListener(this);
                if(kitapDetayAciklamaTextId.lineCount>4){
                    kitapDetayAciklamaTextId.maxLines = 4;
                    viewMoreImageIdLayout.visibility = View.VISIBLE;
                    viewMoreImageId.visibility = View.VISIBLE;
                }
                return true;
            }
        });


        viewMoreImageId.setOnClickListener {
            kitapDetayAciklamaTextId.maxLines = Integer.MAX_VALUE;
            viewMoreImageIdLayout.visibility = View.GONE;
            viewMoreImageId.visibility = View.GONE;
            viewLessImageId.visibility = View.VISIBLE;


        }

        viewLessImageId.setOnClickListener {
            kitapDetayAciklamaTextId.maxLines = 4;
            viewMoreImageIdLayout.visibility = View.VISIBLE;
            viewMoreImageId.visibility = View.VISIBLE;
            viewLessImageId.visibility = View.GONE;
        }

        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapId");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapResim");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAd");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"yazarAd");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAciklama");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapYayinevi");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapAlinmaTarih");
        ProjectUtil.removeFromSharedPreferences(this,ProjectUtil.SHARED_PREF_FILE,"kitapPuan");

        kitapPaylasImage.setOnClickListener {
            if (imagePath != null && kitapAd != null) {
                shareKitapResimWithAd(imagePath,kitapAd);
            }
        }

        yorumlarRelativeLayout.setOnClickListener {
            if(yorumlarLinearLayout.visibility == View.GONE){
                expandCollapseYorumlarImageviewId.setImageResource(R.drawable.ic_expand_less_black_24dp);
                yorumlarLinearLayout.visibility = View.VISIBLE;

                if (kitapPuan != null) {
                    puanRatingBar.rating = ProjectUtil.roundToDecimals(kitapPuan,1);
                    if(ProjectUtil.roundToDecimals(kitapPuan,1) == 0.0f){
                        puanTextView.visibility = View.GONE;
                        puanBosTextViewId.visibility = View.VISIBLE;
                    }else{
                        puanBosTextViewId.visibility = View.GONE;
                        puanTextView.text = "("+ProjectUtil.roundToDecimals(kitapPuan,1)+")";
                    }
                }

                messageTextInputId.editText!!.addTextChangedListener(TextInputErrorClearListener(messageTextInputId));

                yorumKaydetButton.setOnClickListener {
                    val yorumAciklama:String = messageTextInputId.editText?.text.toString();
                    if(TextUtils.isEmpty(yorumAciklama)){
                        messageTextInputId.error = resources.getString(R.string.yorumBosErrorStr);
                        return@setOnClickListener;
                    }

                    if(kisiselPuanRating.rating<1f){
                        SimpleToast.warning(applicationContext, resources.getString(R.string.puanBosErrorStr), "{fa-exclamation-triangle}");
                        return@setOnClickListener;
                    }

                    kitapPuanKaydet(kisiselPuanRating.rating);
                    kitapYorumKaydet(yorumAciklama);
                }

                loadProgressPuanYorumListe();

            }else{
                expandCollapseYorumlarImageviewId.setImageResource(R.drawable.ic_expand_more_gray_24dp);
                yorumlarLinearLayout.visibility = View.GONE;
            }

        }

    }

    private fun kitapPuanKaydet(rating: Float) {
        pd.show();
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("puan",rating.toInt());

        val kitapObj:JSONObject = JSONObject();
        kitapObj.put("id",kitapId);

        jsonObj.put("kitap",kitapObj);

        kitapIslemService.puanKaydet(jsonObj.toString()).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                pd.dismiss();
                SimpleToast.error(applicationContext, applicationContext!!.resources.getString(R.string.puanKayitHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    pd.dismiss();
                }
            }
        });
    }

    private fun kitapYorumKaydet(yorumAciklama: String) {
        pd.show();
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("yorum",yorumAciklama);

        val kitapObj:JSONObject = JSONObject();
        kitapObj.put("id",kitapId);

        jsonObj.put("kitap",kitapObj);

        kitapIslemService.yorumKaydet(jsonObj.toString()).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, applicationContext!!.resources.getString(R.string.yorumKayitHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(applicationContext, applicationContext.resources.getString(R.string.yorumKayitBasarili), "{fa-check}");
                    messageTextInputId.editText?.text = null;
                    kisiselPuanRating.rating = 0f;
                    pd.dismiss();
                    loadProgressPuanYorumListe();
                }
            }
        });
    }

    private fun loadProgressPuanYorumListe(){
        pd.show();
        yorumProgressRwId.setHasFixedSize(true);
        yorumProgressRwId.layoutManager = LinearLayoutManager(applicationContext);

        yorumlarRwId.setHasFixedSize(true);
        yorumlarRwId.layoutManager = LinearLayoutManager(applicationContext);
        yorumlarRwId.addItemDecoration(LinearSpacingDecoration(itemSpacing = 20, edgeSpacing = 30))

        kitapIslemService.getYorumListe(YORUM_LISTE_URL).enqueue(object:
            Callback<YorumListeModel> {
            override fun onFailure(call: Call<YorumListeModel>?, t: Throwable) {
                pd.dismiss();
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapListeHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<YorumListeModel>?, response: Response<YorumListeModel>) {
                val yorumModel:YorumListeModel =  response.body() as YorumListeModel;
                puanListe = yorumModel.puanListe;
                yorumListe = yorumModel.yorumListe;
                val puanAdapter = KitapPuanProgressListeAdapter(applicationContext,puanListe);
                val yorumAdapter = KitapYorumListeAdapter(applicationContext,yorumListe);

                yorumProgressRwId.adapter = puanAdapter;
                yorumlarRwId.adapter = yorumAdapter;

                if(yorumListe != null && yorumListe.size>0){
                    yorumYapilmamisTextViewId.visibility = View.GONE;
                    yorumlarRwId.visibility = View.VISIBLE;
                }else{
                    yorumlarRwId.visibility = View.GONE;
                    yorumYapilmamisTextViewId.visibility = View.VISIBLE;
                }
                pd.dismiss();
            }
        });
    }

    private fun shareKitapResimWithAd(imagePath:String,kitapAd:String) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        val kitapResim: Bitmap? = ProjectUtil.getBitmapFromUrl(imagePath);
        if(kitapResim != null){
            val share:Intent = Intent(Intent.ACTION_SEND);
            share.setType("image/jpg");
            val bytes:ByteArrayOutputStream = ByteArrayOutputStream();
            kitapResim.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            val path:String = MediaStore.Images.Media.insertImage(contentResolver,kitapResim,kitapAd,null);
            val imageUri:Uri = Uri.parse(path);
            share.putExtra(Intent.EXTRA_TEXT, kitapAd+" adlı kitaba bir göz at!");
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Select"));
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }
}
