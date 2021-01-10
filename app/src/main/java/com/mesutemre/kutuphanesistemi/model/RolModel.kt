package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RolModel(
    @SerializedName("id")
    @Expose
    var id:Int,

    @SerializedName("rol")
    @Expose
    var rol: RolTurModel

) {
}