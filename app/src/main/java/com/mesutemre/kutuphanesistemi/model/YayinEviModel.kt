package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class YayinEviModel( @SerializedName("id")
                          @Expose
                          var id:Int,

                          @SerializedName("aciklama")
                          @Expose
                          var aciklama:String,

                          @SerializedName("durum")
                          @Expose
                          var durum: ParametreDurumModel?,

                          @SerializedName("olusturan")
                          @Expose
                          var olusturan:KullaniciModel?) {

    override fun toString(): String {
        return aciklama;
    }
}