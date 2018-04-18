package com.challenge.applaudo.codechallenge.api

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.realm.Realm
import io.realm.RealmConfiguration
import android.media.AudioManager
import android.telephony.TelephonyManager
import android.view.View
import android.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.activities.MainActivity
import com.challenge.applaudo.codechallenge.models.JsonFeed
import com.challenge.applaudo.codechallenge.realmbd.RealmImporter
import com.google.gson.Gson
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.util.HashMap

//class for method reuse
class Globalfun{
    //for laoding local resources by glide
    fun glideback(context: Context?, resourceId:Int?, imageViewResource: ImageView?){
        Glide
                .with(context!!)
                .load(resourceId)
                .into(imageViewResource!!)

    }
    /*funcion de intent*/
    fun gotoActvity(contexto: Activity?, act: Class<*>?, valor: String?,finalizar:Int?) {
        val intent = Intent(contexto, act)
        intent.putExtra("mensaje", valor)
        contexto!!.startActivity(intent)
        if(finalizar==1){
            contexto.finish()
        }
    }
    //inicializar realm
    fun realmInitial(contexto: Context?): Realm {
        var  configglobal:RealmConfiguration?=null
        try {
            Realm.init(contexto!!)
           configglobal =  RealmConfiguration.Builder()
                    .name("bdexample.realm")
                    .schemaVersion(2)
                    .deleteRealmIfMigrationNeeded()
                    .build()
         Realm.getInstance(configglobal!!)
            Realm.setDefaultConfiguration(configglobal)
        } catch (e: Exception) {

        }

        return   Realm.getInstance(configglobal!!)
    }

    @SuppressLint("CheckResult")
//background url glide
    fun backgroundUrl(contexto: Context?, url: String?, img: ImageView?) {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.placeholder)
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(contexto!!)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(img!!)
    }

    @SuppressLint("HardwareIds")
//make calls
    fun makeCall(context:Activity?, phone:String?){
       if(ContextCompat.checkSelfPermission(context!!,
               Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){

           if ((context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number == null) {
               // no phone
           }
           else{
               if(!isCallActive(context)){
                   //Open call
                   val intent = Intent(Intent.ACTION_CALL)
                   intent.data = Uri.parse("tel:$phone")
                   context.startActivity(intent)
               }
           }
       }
    }

   private  fun isCallActive(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    //dialogo error
    fun callDialog( Titulo: String, mensaje: String, TxtBoton: String,
                     contexto: Context?) {
        val dialog = Dialog(contexto, R.style.hidetitle)
        dialog.setContentView(R.layout.dialog_popup)
        // set the custom dialog components - text, image and button
        val close: RelativeLayout = dialog.findViewById(R.id.btn)
        val btnPopup: Button = dialog.findViewById(R.id.btn_Popup)
        val textTitulo: TextView = dialog.findViewById(R.id.textTitulo)
        val textMensaje: TextView = dialog.findViewById(R.id.textMensaje)
        textTitulo.text = Titulo
        textMensaje.text = mensaje
        btnPopup.text = TxtBoton
        dialog.setCancelable(false)
        // Close Button
        close.setOnClickListener {
            dialog.dismiss()
        }
        // Close Button
        btnPopup.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    //detect idf conecttion is usable
    fun isNetworkAvailable(contexto: Context?): Boolean {
        val connectivityManager = contexto!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    //to share content url
    fun shareContent(context:Activity?, data:String?){
        try{
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, data)
            context!!.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.send_to)))
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun descargaGestor(ProgressBar:ProgressBar?,contexto: Context?):JsonFeed?{
        var mjsonfeedArray: JsonFeed?=null
        try{
            ProgressBar!!.visibility = View.VISIBLE
            //parameter of url
            val sufijo = contexto!!.
                    getString(R.string.sufix)
            val params = HashMap<String, String>()
            val callexample = CodeChallenge.instance.mapiService!!.getDatosGETArray(sufijo, params)
            callexample.enqueue(object : Callback<JsonArray> {
                override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                    if (response.isSuccessful) {
                        try {
                            ProgressBar.visibility = View.GONE
                            //obtain json array
                            val mresponsestring = response.body().toString()
                            //asign var string to convert as an object
                            val responsestringmod = "{\"registros\":$mresponsestring}"
                            //convert to object

                                mjsonfeedArray = Gson().fromJson<JsonFeed>(responsestringmod,
                                        JsonFeed::class.java)
                                //save data to realm
                                val mdata = ByteArrayInputStream(mresponsestring.toByteArray())
                                val mImportador = RealmImporter(mdata,CodeChallenge.instance.realm)
                                mImportador.importFromJsonload()
                                //consult realmwith results
                                if (contexto is MainActivity) {
                                    contexto.realmConsultaBd()
                                    contexto.mjsonFeedArray = mjsonfeedArray
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        ProgressBar.visibility = View.GONE
                        //consult bd if data is available
                        if (contexto is MainActivity) {
                            contexto.realmConsultaBd()
                        }
                        println("failed download" + response.errorBody())
                        //function to call dialog
                        CodeChallenge.instance.variablesGlobales!!.callDialog(contexto.getString(R.string.title),
                                contexto.getString(R.string.errorconect1),contexto.getString(R.string.aceptar),
                                contexto)
                    }
                }
                override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                    ProgressBar.visibility = View.GONE
                    //consult bd if data is available
                    if (contexto is MainActivity) {
                        contexto.realmConsultaBd()
                    }
                    //function to call dialog
                    CodeChallenge.instance.variablesGlobales!!.callDialog(contexto.getString(R.string.title),
                            contexto.getString(R.string.errorconect2),contexto.getString(R.string.aceptar),
                            contexto)
                }
            })
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return  mjsonfeedArray
    }

}