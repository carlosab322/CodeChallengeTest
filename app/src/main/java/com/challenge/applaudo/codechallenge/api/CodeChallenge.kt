package com.challenge.applaudo.codechallenge.api

import android.graphics.Typeface
import android.support.multidex.MultiDexApplication
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.retrofit.ApiClient
import com.challenge.applaudo.codechallenge.retrofit.InterfaceRetrofit
import io.realm.Realm
class CodeChallenge : MultiDexApplication() {
    companion object {
        lateinit var instance: CodeChallenge
    }
    init {
        instance = this
    }
    var museoRegular: Typeface?=null
    var arialBold: Typeface?=null
    var arial: Typeface?=null
    var variablesGlobales: Globalfun?=null
    var realm: Realm ?=null
     var mapiService: InterfaceRetrofit? = null
     private var mApiClient: ApiClient? = null
    override fun onCreate() {
        super.onCreate()
        // Initialize Realm. Should only be done once when the application starts.
        try{
            variablesGlobales = Globalfun()
            realm = variablesGlobales!!.realmInitial(applicationContext)

            mApiClient = ApiClient()
            mApiClient!!.url(getString(R.string.resourceurl))
            mapiService = mApiClient!!.getClient().create(InterfaceRetrofit::class.java)
            //fuentes
            museoRegular = Typeface.createFromAsset(applicationContext.assets, "fonts/MuseoRegular.ttf")
            arialBold = Typeface.createFromAsset(applicationContext.assets, "fonts/ArialBold.ttf")
            arial = Typeface.createFromAsset(applicationContext.assets, "fonts/Arial.ttf")

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun dpToPx(dp: Int): Int {
        val density = applicationContext.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

}