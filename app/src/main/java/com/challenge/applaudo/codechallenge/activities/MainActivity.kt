package com.challenge.applaudo.codechallenge.activities

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.adapter.RecyclerAdapter
import com.challenge.applaudo.codechallenge.api.CodeChallenge
import com.challenge.applaudo.codechallenge.fragment.FragmentContentVideo
import com.challenge.applaudo.codechallenge.fragment.Fragment_Home
import com.challenge.applaudo.codechallenge.models.JsonFeed
import com.challenge.applaudo.codechallenge.realmbd.JsonFeedBd
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions
import android.view.*


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    //retrofit references
    //vars for download
     var mjsonFeedArray: JsonFeed?=null
    //realm utils
    private var resultRealm: RealmResults<JsonFeedBd>?=null
    //object for individual query
    var mresultrealm: RealmResults<JsonFeedBd>?=null
    //permissions
    var firstime:Boolean = true
    private val permisosApp = 124
    private var mContext: Context = this
    //for detection in landscape
    var mislandscape :Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try{
            //call header portrait
            loadHeader()
            //call onrefresh
            onrefresh()
            //call permissions android 6 and above
            permisos()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    //funcion para consulta de realm
     fun realmConsultaBd() = try{
         //consult realmwith results
         resultRealm = CodeChallenge.instance.realm!!.where(JsonFeedBd::class.java).findAll()
         //validate if not empty
         if(resultRealm!!.size>0){
             textempty.visibility = View.GONE
             loadlistview()
         }
         else{
             textempty.visibility = View.VISIBLE
         }
     }
     catch (e:Exception){
         e.printStackTrace()
     }
    override fun onResume() {
        super.onResume()
        try {
            orientationRotation()
            //call download json feed
            if(CodeChallenge.instance.variablesGlobales!!.isNetworkAvailable(this)){
                textempty.visibility = View.GONE
                downloadJson()
            }
            else{
                //consult bd if data is available
                realmConsultaBd()
                //function to call dialog
                CodeChallenge.instance.variablesGlobales!!.callDialog(getString(R.string.title),
                        getString(R.string.errorconect),getString(R.string.aceptar),
                        this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //para detectar nuevos cambios de orientacion de tablet

    private  fun orientationRotation(){
        try{

            val tabletSize = resources.getBoolean(R.bool.isTablet)
            if (tabletSize) {
               cargarLandScape(340)

            } else {
                cargarPortrait()
            }
        }
        catch (e:Exception ){
            e.printStackTrace()
        }

    }

    private  fun cargarLandScape( tamano:Int){
        try{
            mislandscape=true
            //define dimension for list
            val paramscontent = RelativeLayout.LayoutParams(CodeChallenge.instance.dpToPx(tamano),
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            paramscontent.addRule(RelativeLayout.BELOW,R.id.containerHeader)
            simpleSwipeRefreshLayout.layoutParams = paramscontent
            loadHeader()
            //call position 1 for list in fragment
            resultadoRealmporId(1)

            if(mresultrealm!!.size>0){
                fragmentHome()
            }
            loadlistview()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }


    private  fun cargarPortrait(){
        try{
            mislandscape=false
            //define dimension for list
            val paramscontent = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            paramscontent.addRule(RelativeLayout.BELOW,R.id.containerHeader)
            simpleSwipeRefreshLayout.layoutParams = paramscontent
            loadHeader()
            resultadoRealmporId(1)
            if(resultRealm!!.size>0){
                fragmentHome()
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    //para cambios de orientacion en pantalla
    private fun cambioPantallaOrientacion(newConfig: Configuration?){
        try {
            // Checks the orientation of the screen
            if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                cargarLandScape(260)
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
               cargarPortrait()
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null) {
         cambioPantallaOrientacion(newConfig)
        }

    }
    //to call download of json_feed via retrofit
    private  fun downloadJson(){
        try {
             CodeChallenge.instance.
                    variablesGlobales!!.descargaGestor(ProgressBar,mContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //method to call an individual object realm
    fun resultadoRealmporId(position:Int){
        try{
            mresultrealm = CodeChallenge.instance.realm!!.where(JsonFeedBd::class.java).
                    equalTo("id",position).findAll()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    //method to call the body detail fragment
    fun fragmentHome(){
        try {

            supportFragmentManager.beginTransaction()
                    .replace(R.id.containerfragmentvid, FragmentContentVideo.getInstance(mresultrealm))
                    .addToBackStack(null)
                    .commit()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //method to call the header fragment
    private fun loadHeader(){
        try {
            if(resultRealm!=null){
                if(resultRealm!!.size>0){
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.containerHeader, Fragment_Home.getInstance(resultRealm!![0]!!
                                    .phone_number,resultRealm!![0]!!.tickets_url))
                            .commit()
                }
                else{
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.containerHeader, Fragment_Home.getInstance("",""))
                            .commit()
                }
            }
            else{
                supportFragmentManager.beginTransaction()
                        .replace(R.id.containerHeader, Fragment_Home.getInstance("",""))
                        .commit()
            }
        } catch (e: Exception) {
        }
    }
    //List load method with data
    private fun loadlistview(){
        //reciclerview
      //  listfeed.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        listfeed.layoutManager = linearLayoutManager
        //adapter
        if(resultRealm!=null){
            val adapter = RecyclerAdapter(resultRealm,this,firstime)
            listfeed.adapter = adapter
            adapter.notifyDataSetChanged()
        }


    }

    //to refresh on swipe the list
    private  fun onrefresh(){
        // implement  event on SwipeRefreshLayout
        simpleSwipeRefreshLayout.setOnRefreshListener({
            // implement Handler to wait for 3 seconds and then update UI
            Handler().postDelayed({
                simpleSwipeRefreshLayout.isRefreshing = false
                downloadJson()
            }, 2500)
        })
    }
    //memory fun glide
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }
    ///permissions android 6.0 and above
    private fun permisos() {
        val perms = arrayOf( Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.consejopermisos),
                    permisosApp, *perms)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
    }
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }


}

