package com.mesutemre.kutuphanesistemi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.mesutemre.kutuphanesistemi.util.ProjectUtil

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var c:Context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        if(!ProjectUtil.internetAccessControl(this)){
            ProjectUtil.showToastMessage(this,"İnternet erişiminiz yoktur!!!",true);
            finishAffinity();
        }else{
            setContentView(R.layout.activity_splash_screen);
            this.c = this;
            val handler:Handler = Handler(Looper.getMainLooper());
            handler.postDelayed(object:Runnable{
                override fun run() {
                    if(ProjectUtil.getStringDataFromSharedPreferences(c,
                            ProjectUtil.SHARED_PREF_FILE,"kullaniciAdi") != null){
                        ProjectUtil.activityYonlendir(c,MainActivity());
                    }else{
                        ProjectUtil.activityYonlendir(c,LoginActivity());
                    }
                }

            },1000);
        }
    }
}
