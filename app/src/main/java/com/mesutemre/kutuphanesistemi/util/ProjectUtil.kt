package com.mesutemre.kutuphanesistemi.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.mesutemre.kutuphanesistemi.model.BaseObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ProjectUtil {

    companion object{

        val SHARED_PREF_FILE:String = "KutuphaneSistem";
        val API_URL:String = "http://192.168.1.106:8080/KutuphaneSistemiWS/";

        /**
         * @author                      Mesut Emre Çelenk
         * @param mevcutActivity        İçinde bulunduğunuz Activity
         * @param gidilecekActivity     Gitmek istediğiniz Activity
         * @sınce Ağustos 2020
         */
        fun activityYonlendir(mevcutActivity:Context,gidilecekActivity:Context):Unit{
            val intent = Intent(mevcutActivity,gidilecekActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mevcutActivity.startActivity(intent);
        }

        /**
         * @author                      Mesut Emre Çelenk
         * @param mevcutActivity        İçinde bulunduğunuz Activity
         * @param gidilecekActivity     Gitmek istediğiniz Activity
         * @param sayfaParams           Bir sonrakği activity de almak istediğiniz parametreler
         * @sınce Ağustos 2020
         */
        fun activityYonlendir(mevcutActivity:Context,gidilecekActivity:Context,sayfaParams:Map<String,String>):Unit{
            val intent = Intent(mevcutActivity,gidilecekActivity::class.java);
            sayfaParams.forEach{(key,value) ->
                intent.putExtra(key,value);
            }
            mevcutActivity.startActivity(intent);
        }

        /**
         * @author                      Mesut Emre Çelenk
         * @param mevcutActivity        İçinde bulunduğunuz Activity
         * @param gidilecekActivity     Gitmek istediğiniz Activity
         * @param sayfaParamObject      Bir sonrakği activity de almak istediğiniz nesne bu sınıfın içine gömülür
         * @param key                   Bir sonraki activityde almak istediğiniz nesneyi bu key ile alabilirsiniz.
         * @sınce Ağustos 2020
         */
        fun activityYonlendir(mevcutActivity:Context, gidilecekActivity:Context, sayfaParamObject: BaseObject, key:String):Unit{
            val intent = Intent(mevcutActivity,gidilecekActivity::class.java);
            intent.putExtra(key, sayfaParamObject);
            mevcutActivity.startActivity(intent);
        }

        fun anaEkranaDon(c:Context):Unit{
            val yeniIntent = Intent(Intent.ACTION_MAIN);
            yeniIntent.addCategory(Intent.CATEGORY_HOME);
            yeniIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            c.startActivity(yeniIntent);
        }

        /**
         * @author                      Mesut Emre Çelenk
         * @param mevcutActivity        İçinde bulunduğunuz Activity
         * @param donulecekActivity     Geri tuşu ile dönmek istediğiniz Activity
         * @sınce Ağustos 2020
         */
        fun backStackToWantedActivity(mevcutActivity:Context,donulecekActivity:Context):Unit{
            val yeniIntent = Intent(mevcutActivity,donulecekActivity::class.java);
            yeniIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mevcutActivity.startActivity(yeniIntent);
        }

        fun showToastMessage(c:Context,mesaj:String,uzunMesaj:Boolean):Unit{
            var mesajTip:Int = Toast.LENGTH_SHORT;
            if(uzunMesaj){
                mesajTip = Toast.LENGTH_LONG;
            }
            Toast.makeText(c,mesaj,mesajTip).show();
        }

        /**
         * @author                      Mesut Emre Çelenk
         * @param c                     İçinde bulunduğunuz Activity
         * @param theme                 component için style'da kayıtlı olan temamız
         * @param saat                  saat
         * @param dakika                dakika
         * @param is24Hour              24 saatlik zaman dilimimi olsun?
         * @sınce Eylül 2020
         */
        fun getTimePicker(c:Context,theme:Int,saat:Int,dakika:Int,is24Hour:Boolean):TimePickerDialog{
            val timePicker = TimePickerDialog(c,
                theme,TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                },saat,dakika,is24Hour);
            return timePicker;
        }


        /**
         * @author                      Mesut Emre Çelenk
         * @param c                     İçinde bulunduğunuz Activity
         * @param theme                 component için style'da kayıtlı olan temamız
         * @param gun                   gün
         * @param ay                    ay
         * @param yil                   yıl
         * @sınce Eylül 2020
         */
        fun getDatePicker(c:Context,theme:Int,gun:Int,ay:Int,yil:Int):DatePickerDialog{
            val datePicker = DatePickerDialog(c,theme,DatePickerDialog.OnDateSetListener { datePicker, y, a, g ->
            },yil,ay,gun);
            return datePicker;
        }

        fun putStringDataToSharedPreferences(c:Context,name:String,key:String,value:String){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();

            editor.putString(key,value);

            editor.commit();
        }

        fun putIntDataToSharedPreferences(c:Context,name:String,key:String,value:Int){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();

            editor.putInt(key,value);

            editor.commit();
        }

        fun putLongDataToSharedPreferences(c:Context,name:String,key:String,value:Long){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();

            editor.putLong(key,value);

            editor.commit();
        }

        fun putBooleanDataToSharedPreferences(c:Context,name:String,key:String,value:Boolean){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();

            editor.putBoolean(key,value);

            editor.commit();
        }

        fun putDoubleDataToSharedPreferences(c:Context,name:String,key:String,value:Float){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();

            editor.putFloat(key,value);

            editor.commit();
        }

        fun getStringDataFromSharedPreferences(c:Context,name:String,key:String):String?{
            val sp = c.getSharedPreferences(name, Context.MODE_PRIVATE);
            return sp.getString(key,null);
        }

        fun getIntDataFromSharedPreferences(c:Context,name:String,key:String):Int{
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            return sp.getInt(key,0);
        }

        fun getLongDataFromSharedPreferences(c:Context,name: String,key: String):Long{
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            return sp.getLong(key,0);
        }

        fun getBooleanDataFromSharedPreferences(c:Context,name:String,key: String):Boolean{
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            return sp.getBoolean(key,false);
        }

        fun getFloatDataFromSharedPreferences(c:Context,name: String,key: String):Float{
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            return sp.getFloat(key,0f);
        }

        fun removeFromSharedPreferences(c:Context,name:String,key:String){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();
            editor.remove(key);

            editor.commit();
        }

        fun internetAccessControl(context:Context):Boolean{
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            }
            return false
        }

        fun getBitmapResourceFromBase64(base64Value:String):Bitmap{
            val decodedString:ByteArray = Base64.decode(base64Value.toByteArray(),
                Base64.DEFAULT);
            val decodedByte:Bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.size);
            return decodedByte;
        }

        fun deleteAllSharedPreferencesData(c:Context,name:String){
            val sp = c.getSharedPreferences(name,Context.MODE_PRIVATE);
            val editor = sp.edit();
            editor.clear().commit();
        }

        fun formatDate(date:Date,pattern:String):String{
            val sdf:SimpleDateFormat = SimpleDateFormat(pattern);
            return sdf.format(date);
        }

        fun getImageUriFromBitmap(c:Context,inImage:Bitmap):Uri{
            var path:String = "";
            try {
                val bytes:ByteArrayOutputStream = ByteArrayOutputStream();
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                path = MediaStore.Images.Media.insertImage(c.getContentResolver(), inImage, "KUT_SIS_IMG_"+System.currentTimeMillis(),null);

            }catch (e:Exception){
                Log.d("Exception",e.localizedMessage);
                e.printStackTrace();
            }
            return Uri.parse(path);
        }

        fun getPath(context:Context,uri:Uri):String?{
            val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null;
        }

        fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                if (cursor != null) cursor.close()
            }
            return null
        }

        private fun isExternalStorageDocument(uri:Uri):Boolean {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        private fun isDownloadsDocument(uri:Uri):Boolean {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        private fun isMediaDocument(uri:Uri):Boolean {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        fun getBitmapFromUrl(url:String): Bitmap? {
            var input: InputStream? = null;
            var bitMap:Bitmap? = null;
            try {
                val url:URL = URL(url);
                val connection:HttpURLConnection = url.openConnection() as HttpURLConnection;
                connection.setDoInput(true);
                connection.connect();
                input = connection.inputStream;
                bitMap = BitmapFactory.decodeStream(input);
            }
            catch (e: Exception) {
                e.printStackTrace();
            }
            finally {
                input?.close();
            }
            return bitMap;
        }

        fun roundToDecimals(value:Float,decimals: Int): Float {
            var dotAt = 1
            repeat(decimals) { dotAt *= 10 }
            val roundedValue = (value * dotAt).roundToInt()
            return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
        }

        fun loadImageFromUrl(url:String):Drawable?{
            if (android.os.Build.VERSION.SDK_INT > 9) {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            var inputStream:InputStream? = null;
            var resim:Drawable? = null;

            try {
                inputStream = URL(url).getContent() as InputStream;
                resim = Drawable.createFromStream(inputStream,"img_"+Date().time);
                return resim;
            }catch (e:java.lang.Exception){
                e.printStackTrace();
            } finally {
                inputStream?.close();
            }

            return null;
        }
    }
}