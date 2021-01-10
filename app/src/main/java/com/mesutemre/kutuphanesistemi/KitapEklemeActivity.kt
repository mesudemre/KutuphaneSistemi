package com.mesutemre.kutuphanesistemi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.pierry.simpletoast.SimpleToast
import com.google.android.material.textfield.TextInputLayout
import com.mesutemre.kutuphanesistemi.listeners.TextInputErrorClearListener
import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YayinEviModel
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.service.IParametreService
import com.mesutemre.kutuphanesistemi.util.ProjectUtil
import com.mesutemre.kutuphanesistemi.util.WebApiUtil
import fr.ganfra.materialspinner.MaterialSpinner
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class KitapEklemeActivity : AppCompatActivity() {

    private lateinit var kitapImage:ImageView;
    private lateinit var kitapAdEditText:TextInputLayout;
    private lateinit var yazarAdEditText:TextInputLayout;
    private lateinit var yayinEviCombo:MaterialSpinner;
    private lateinit var kitapTurCombo:MaterialSpinner;
    private lateinit var alinmaTarEditText:TextInputLayout;
    private lateinit var kitapAciklamaEditText:TextInputLayout;
    private lateinit var kitapKaydetButton: Button;
    private lateinit var pd: Dialog;

    private lateinit var parametreService:IParametreService;
    private lateinit var kitapIslemService:IKitapIslemService;
    private var yeniYayinEviListe:ArrayList<YayinEviModel> = ArrayList<YayinEviModel>();
    private var yeniKitapTurListe:ArrayList<IlgiAlanlariParametreModel> = ArrayList<IlgiAlanlariParametreModel>();
    private var kitapImageChanged:Boolean = false
    private lateinit var selectedImageUri: Uri;

    companion object {
        const val CAMERA_REQUEST_CODE = 11223;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitap_ekleme);

        val actionbar = supportActionBar;
        actionbar!!.title = resources.getString(R.string.kitapEkleActivityTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    @SuppressLint("RestrictedApi")
    private fun initComponents():Unit{
        pd = Dialog(this);
        val pdView: View = layoutInflater.inflate(R.layout.custom_progress_dialog,null);
        pd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setContentView(pdView);
        pd.setCancelable(false);
        //pd.show();

        this.kitapImage             = findViewById(R.id.kitapImageId) as ImageView;
        this.kitapAdEditText        = findViewById(R.id.kitapAdTextInputId) as TextInputLayout;
        this.yazarAdEditText        = findViewById(R.id.yazarAdTextInputId) as TextInputLayout;
        this.yayinEviCombo          = findViewById(R.id.yayinEviSpinnerId) as MaterialSpinner;
        this.kitapTurCombo          = findViewById(R.id.kitapTurSpinnerId) as MaterialSpinner;
        this.alinmaTarEditText      = findViewById(R.id.alinmaTarTextInputId) as TextInputLayout;
        this.kitapAciklamaEditText  = findViewById(R.id.kitapAciklamatextInputId) as TextInputLayout;
        this.kitapKaydetButton      = findViewById(R.id.kitapKaydetButton) as Button;

        this.parametreService       = WebApiUtil.getParametreService(this);
        this.kitapIslemService      = WebApiUtil.getKitapService(this);

        this.kitapAciklamaEditText.editText?.setOnTouchListener { v, event ->
            if (v.id == R.id.aciklamaEtId) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        this.alinmaTarEditText.editText?.setOnClickListener {
            val cal:Calendar = Calendar.getInstance();
            val yil:Int = cal.get(Calendar.YEAR);
            val ay:Int = cal.get(Calendar.MONTH);
            val gun:Int = cal.get(Calendar.DAY_OF_MONTH);

            val datePicker = DatePickerDialog(this,R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { datePicker, y, a, g ->
                    alinmaTarEditText.editText?.setText("$y-${a+1}-$g"); //$g.${a+1}.$y
                },yil,ay,gun);

            datePicker.setTitle(resources.getString(R.string.selectTarih));
            datePicker.setButton(DialogInterface.BUTTON_POSITIVE,resources.getString(R.string.ayarlaLabel),datePicker);
            datePicker.setButton(DialogInterface.BUTTON_NEGATIVE,resources.getString(R.string.iptalLabel),datePicker);

            datePicker.show();
        }

        yazarAdEditText.editText!!.addTextChangedListener(TextInputErrorClearListener(yazarAdEditText));
        kitapAdEditText.editText!!.addTextChangedListener(TextInputErrorClearListener(kitapAdEditText));
        kitapAciklamaEditText.editText!!.addTextChangedListener(TextInputErrorClearListener(kitapAciklamaEditText));

        fillYayinEviCombo();
        fillKitapTurCombo();

        kitapImage.setOnClickListener {
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }

        kitapKaydetButton.setOnClickListener {
            val kitapAd:String          = kitapAdEditText.editText?.text.toString();
            val yazarAd:String          = yazarAdEditText.editText?.text.toString();
            val kitapAciklama:String    = kitapAciklamaEditText.editText?.text.toString();
            val alinmaTarih:String      = alinmaTarEditText.editText?.text.toString();
            var selectedKitapTur        = this.kitapTurCombo.selectedItem;
            var selectedYayinevi        = this.yayinEviCombo.selectedItem;

            if(TextUtils.isEmpty(kitapAd)){
                kitapAdEditText.error = resources.getString(R.string.kitapAdValidErr);
                return@setOnClickListener;
            }
            if(TextUtils.isEmpty(yazarAd)){
                yazarAdEditText.error = resources.getString(R.string.yazarAdValidErr);
                return@setOnClickListener;
            }

            if(selectedKitapTur == null){
                kitapTurCombo.error = resources.getString(R.string.kitapTurValidErr);
                return@setOnClickListener;
            }


            if(selectedYayinevi == null){
                yayinEviCombo.error = resources.getString(R.string.yayinEviValidErr)
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(kitapAciklama)){
                kitapAciklamaEditText.error = resources.getString(R.string.kitapAciklamaValidErr);
                return@setOnClickListener;
            }

            if(TextUtils.isEmpty(alinmaTarih)){
                alinmaTarEditText.error = resources.getString(R.string.alinmaTarValidErr);
                return@setOnClickListener;
            }

            if(!kitapImageChanged){
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapImageValidErr), "{fa-times-circle}");
                return@setOnClickListener;
            }

            pd.show();

            selectedKitapTur = this.kitapTurCombo.selectedItem as IlgiAlanlariParametreModel;
            selectedYayinevi = this.yayinEviCombo.selectedItem as YayinEviModel;

            val jsonObj:JSONObject = JSONObject();
            jsonObj.put("kitapAd",kitapAd);
            jsonObj.put("yazarAd",yazarAd);

            val kitapTurObj:JSONObject = JSONObject();
            kitapTurObj.put("id",selectedKitapTur.id);
            jsonObj.put("kitapTur",kitapTurObj);

            val yayinEviObj:JSONObject = JSONObject();
            yayinEviObj.put("id",selectedYayinevi.id);
            jsonObj.put("yayinEvi",yayinEviObj);

            jsonObj.put("alinmatarihi",alinmaTarih);
            jsonObj.put("kitapAciklama",kitapAciklama);

            kitapKaydet(jsonObj.toString());
        }

    }

    private fun kitapKaydet(jsonStr: String) {
        kitapIslemService.kitapKaydet(jsonStr).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, applicationContext!!.resources.getString(R.string.kitapKayitHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    kitapResimYukle(respModel.statusMessage);
                }
            }
        });
    }

    private fun kitapResimYukle(kitapId:String){
        val kitapIdParam: RequestBody = RequestBody.create(MediaType.parse("text/plain"),kitapId);
        val originalFile: File =  org.apache.commons.io.FileUtils.getFile(ProjectUtil.getPath(applicationContext,selectedImageUri));
        val fileParam: RequestBody = RequestBody.create(MediaType.parse(applicationContext.contentResolver.getType(selectedImageUri)),originalFile);
        val file: MultipartBody.Part = MultipartBody.Part.createFormData("file",originalFile.name,fileParam);
        kitapIslemService.kitapResimYukle(file,kitapIdParam).enqueue(object:
            Callback<ResponseStatusModel> {
            override fun onFailure(call: Call<ResponseStatusModel>?, t: Throwable) {
                SimpleToast.error(applicationContext, applicationContext.resources.getString(R.string.kitapResimYuklemeHata), "{fa-times-circle}");
                pd.dismiss();
            }

            override fun onResponse(call: Call<ResponseStatusModel>?, response: Response<ResponseStatusModel>) {
                if(response.body() != null){
                    val respModel = response.body() as ResponseStatusModel;
                    SimpleToast.info(applicationContext, applicationContext.resources.getString(R.string.kitapKayitBasarli), "{fa-check}");
                    pd.dismiss();
                    ProjectUtil.putStringDataToSharedPreferences(applicationContext,ProjectUtil.SHARED_PREF_FILE,"navfragment","KitapIslemFragment");
                    ProjectUtil.activityYonlendir(applicationContext,MainActivity());
                }
            }
        });
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    private fun fillYayinEviCombo(){
        parametreService.getYayinEviListe().enqueue(object: Callback<ArrayList<YayinEviModel>> {
            override fun onFailure(call: Call<ArrayList<YayinEviModel>>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.yayinEviListeHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<ArrayList<YayinEviModel>>?, response: Response<ArrayList<YayinEviModel>>) {
                yeniYayinEviListe = response.body() as ArrayList<YayinEviModel>;
                val adapter:ArrayAdapter<YayinEviModel> = ArrayAdapter<YayinEviModel>(applicationContext,android.R.layout.simple_spinner_dropdown_item,yeniYayinEviListe);
                yayinEviCombo.adapter = adapter;
            }
        });
    }

    private fun fillKitapTurCombo(){
        val jsonObj: JSONObject = JSONObject();
        jsonObj.put("durum","AKTIF");
        parametreService.getKitapturListe(jsonObj.toString()).enqueue(object: Callback<ArrayList<IlgiAlanlariParametreModel>> {
            override fun onFailure(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, t: Throwable) {
                SimpleToast.error(applicationContext, resources.getString(R.string.kitapTurListeHata), "{fa-times-circle}");
            }

            override fun onResponse(call: Call<ArrayList<IlgiAlanlariParametreModel>>?, response: Response<ArrayList<IlgiAlanlariParametreModel>>) {
                yeniKitapTurListe = response.body() as ArrayList<IlgiAlanlariParametreModel>;
                val adapter:ArrayAdapter<IlgiAlanlariParametreModel> = ArrayAdapter<IlgiAlanlariParametreModel>(applicationContext,android.R.layout.simple_spinner_dropdown_item,yeniKitapTurListe);
                kitapTurCombo.adapter = adapter;
            }
        });
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = data!!.getExtras()?.get("data") as Bitmap;
            kitapImage.setImageBitmap(photo);

            this.selectedImageUri = ProjectUtil.getImageUriFromBitmap(applicationContext,photo);
            this.kitapImageChanged = true;
        }
    }
}
