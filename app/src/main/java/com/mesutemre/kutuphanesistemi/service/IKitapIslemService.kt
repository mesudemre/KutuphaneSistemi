package com.mesutemre.kutuphanesistemi.service

import com.mesutemre.kutuphanesistemi.model.KitapModel
import com.mesutemre.kutuphanesistemi.model.KitapTurIstatistikModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YorumListeModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IKitapIslemService {

    @Headers("Content-Type: application/json")
    @POST("api/kitap/liste")
    fun getKitapListe(@Body jsonStr: String): Call<ArrayList<KitapModel>>;

    @Headers("Content-Type: application/json")
    @POST("api/kitap/kaydet")
    fun kitapKaydet(@Body jsonStr:String):Call<ResponseStatusModel>;

    @Multipart
    @POST("api/kitap/resim/yukle")
    fun kitapResimYukle(@Part file: MultipartBody.Part, @Part("kitapId") username: RequestBody):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @POST("api/kitap/yorum/kaydet")
    fun yorumKaydet(@Body jsonStr:String):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @POST("api/kitap/puan/kaydet")
    fun puanKaydet(@Body jsonStr:String):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @GET()
    fun getYorumListe(@Url url:String): Call<YorumListeModel>;

    @Headers("Content-Type: application/json")
    @GET("api/kitap/tur/istatistik")
    fun getKitapTurIstatistikListe(): Call<ArrayList<KitapTurIstatistikModel>>;
}