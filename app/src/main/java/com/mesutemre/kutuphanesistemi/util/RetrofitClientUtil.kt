package com.mesutemre.kutuphanesistemi.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitClientUtil {

    companion object{

        fun getClient(): Retrofit{
            return Retrofit.Builder()
                .baseUrl(ProjectUtil.API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        fun getClient(c:Context): Retrofit{
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder();
            httpClient.addInterceptor(object:Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request:Request = chain.request().newBuilder().addHeader("Authorization",
                        "Bearer "+ProjectUtil.getStringDataFromSharedPreferences(c,ProjectUtil.SHARED_PREF_FILE,"token")?.trim()).build();
                    return chain.proceed(request);
                }
            });
            return Retrofit.Builder()
                .baseUrl(ProjectUtil.API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        }
    }
}