package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KitapPuanModel(

    @SerializedName("puan")
    @Expose
    val puan:Int?,

    @SerializedName("adet")
    @Expose
    val adet:Int?

) {
}