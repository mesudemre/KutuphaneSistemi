package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName;
import java.util.*

data class KullaniciModel(
    @SerializedName("username")
    @Expose
    var username:String,

    @SerializedName("password")
    @Expose
    var password:String,

    @SerializedName("ad")
    @Expose
    var ad:String,

    @SerializedName("soyad")
    @Expose
    var soyad:String,

    @SerializedName("dogumTarihi")
    @Expose
    var dogumTarihi:Date,

    @SerializedName("resim")
    @Expose
    var resim:String,

    @SerializedName("eposta")
    @Expose
    var eposta:String,

    @SerializedName("cinsiyet")
    @Expose
    var cinsiyet:CinsiyetModel,

    @SerializedName("haberdarmi")
    @Expose
    var haberdarmi:Boolean,


    @SerializedName("ilgiAlanlari")
    @Expose
    var ilgiAlanlari:List<IlgiAlanlariParametreModel>,

   @SerializedName("roller")
   @Expose
   var roller:List<RolModel>

    ) {
}