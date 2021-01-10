package com.mesutemre.kutuphanesistemi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorStatusModel(
    @SerializedName("statusCode")
    @Expose
    var statusCode:Int,

    @SerializedName("statusMessage")
    @Expose
    var statusMessage:String
) {
}