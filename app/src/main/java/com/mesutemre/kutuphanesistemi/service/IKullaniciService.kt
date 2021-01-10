package com.mesutemre.kutuphanesistemi.service

import com.mesutemre.kutuphanesistemi.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IKullaniciService {

    @POST("login")
    fun login(@Body user:AccountCredentials):Call<String>;

    @GET("api/kullanici/bilgi")
    fun getKullaniciBilgi(@Header("Authorization") token:String):Call<KullaniciModel>;

    @GET("api/kullanici/resim")
    fun getKullaniciResim():Call<KullaniciResimModel>;

    @Headers("Content-Type: application/json")
    @POST("api/kullanici/guncelle")
    fun kullaniciGuncelle(@Body jsonStr: String):Call<ResponseStatusModel>;

    @Multipart
    @POST("api/kullanici/resim/yukle")
    fun kullaniciResimGuncelle(@Part file:MultipartBody.Part , @Part("username") username:RequestBody):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @POST("api/kullanici/kaydet")
    fun kullaniciKaydet(@Body jsonStr: String):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @GET("api/parametre/kitapturler")
    fun getKitapturListe(): Call<ArrayList<IlgiAlanlariParametreModel>>;

}