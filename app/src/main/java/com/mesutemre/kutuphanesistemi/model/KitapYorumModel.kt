package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class KitapYorumModel(
    @SerializedName("id")
    @Expose
    val id:Int?,

    @SerializedName("yorum")
    @Expose
    val yorum:String?,

    @SerializedName("kitap")
    @Expose
    val kitap:KitapModel?,

    @SerializedName("olusturan")
    @Expose
    val olusturan:KullaniciModel?,

    @SerializedName("olusturmaTar")
    @Expose
    val olusturmaTar: Date?,

    @SerializedName("puan")
    @Expose
    val puan: Int?,

    @SerializedName("kullaniciResim")
    @Expose
    val kullaniciResim: String?

    ) {


}