package com.mesutemre.kutuphanesistemi.service

import com.mesutemre.kutuphanesistemi.model.IlgiAlanlariParametreModel
import com.mesutemre.kutuphanesistemi.model.ResponseStatusModel
import com.mesutemre.kutuphanesistemi.model.YayinEviModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface IParametreService {

    @GET("api/parametre/yayinevi/liste")
    fun getYayinEviListe(): Call<ArrayList<YayinEviModel>>;

    @Headers("Content-Type: application/json")
    @POST("api/parametre/yayinevi/kaydet")
    fun yayinEviKaydet(@Body jsonStr: String):Call<ResponseStatusModel>;

    @Headers("Content-Type: application/json")
    @POST("api/parametre/kitaptur/liste")
    fun getKitapturListe(@Body jsonStr: String): Call<ArrayList<IlgiAlanlariParametreModel>>;

    @Headers("Content-Type: application/json")
    @POST("api/parametre/kitaptur/kaydet")
    fun kitapTurKaydet(@Body jsonStr: String):Call<ResponseStatusModel>;
}