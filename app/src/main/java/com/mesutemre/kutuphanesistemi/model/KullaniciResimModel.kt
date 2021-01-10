package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KullaniciResimModel(
    @SerializedName("username")
    @Expose
    var username:String,

    @SerializedName("userImageBase64")
    @Expose
    var userImageBase64: String
) {
}