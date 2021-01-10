package com.mesutemre.kutuphanesistemi.util

import android.content.Context
import com.mesutemre.kutuphanesistemi.service.IKitapIslemService
import com.mesutemre.kutuphanesistemi.service.IKullaniciService
import com.mesutemre.kutuphanesistemi.service.IParametreService

class WebApiUtil {

    companion object{

        fun getKullaniciService():IKullaniciService{
            return RetrofitClientUtil.getClient().create(IKullaniciService::class.java);
        }

        fun getKullaniciService(c:Context):IKullaniciService{
            return RetrofitClientUtil.getClient(c).create(IKullaniciService::class.java);
        }

        fun getParametreService(c:Context):IParametreService{
            return RetrofitClientUtil.getClient(c).create(IParametreService::class.java);
        }

        fun getKitapService(c:Context):IKitapIslemService{
            return RetrofitClientUtil.getClient(c).create(IKitapIslemService::class.java);
        }
    }
}