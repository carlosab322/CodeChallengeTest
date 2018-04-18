package com.challenge.applaudo.codechallenge.activities
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.api.CodeChallenge
import com.challenge.applaudo.codechallenge.fragment.FragmentContentVideo
import com.challenge.applaudo.codechallenge.fragment.FragmentHeaderDetail
import com.challenge.applaudo.codechallenge.realmbd.JsonFeedBd
import io.realm.RealmResults

class DetailActivity : FragmentActivity() {
    //realm utils
    private var mresultrealm: RealmResults<JsonFeedBd>?=null
    private var mphone :String =""
    private var mshare :String=""
    private var mid:String ="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }
    //method to call the header fragment
    private fun FragmentHeader(){
        try {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.containerHeader, FragmentHeaderDetail.
                            getInstance(mphone,mshare))
                    .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //method to call the body detail fragment
    private fun FragmentDetalle(){
        try {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.contentmultimedia, FragmentContentVideo.getInstance(
                            mresultrealm))
                    .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        try {
            //get id
            val extras = intent.extras
            mid = extras.getString("mensaje")
            //asign values for fragment header and make consult in realm via id
            if(mid.isNotEmpty()){
                mresultrealm = CodeChallenge.instance.realm!!.where(JsonFeedBd::class.java).
                        equalTo("id",mid.toInt()).findAll()
                if(mresultrealm!!.size>0){
                    mphone= mresultrealm!![0]!!.phone_number
                    mshare= mresultrealm!![0]!!.tickets_url
                }
            }
            //call header frag
            FragmentHeader()
            //call body fragment
            FragmentDetalle()
        } catch (e: Exception) {
        }
    }
}
