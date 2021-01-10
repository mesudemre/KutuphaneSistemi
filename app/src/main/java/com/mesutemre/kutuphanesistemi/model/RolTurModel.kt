package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RolTurModel (
    @SerializedName("value")
    @Expose
    var value:String,

    @SerializedName("label")
    @Expose
    var label:String
) {
}