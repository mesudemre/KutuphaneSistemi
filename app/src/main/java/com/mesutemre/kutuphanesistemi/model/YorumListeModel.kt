package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class YorumListeModel(

    @SerializedName("puanListe")
    @Expose
    val puanListe:ArrayList<KitapPuanModel>,

    @SerializedName("yorumListe")
    @Expose
    val yorumListe:ArrayList<KitapYorumModel>

) {
}