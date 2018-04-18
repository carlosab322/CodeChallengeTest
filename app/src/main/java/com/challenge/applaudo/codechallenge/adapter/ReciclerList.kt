package com.challenge.applaudo.codechallenge.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.activities.DetailActivity
import com.challenge.applaudo.codechallenge.activities.MainActivity
import com.challenge.applaudo.codechallenge.api.CodeChallenge
import com.challenge.applaudo.codechallenge.realmbd.JsonFeedBd
import io.realm.RealmResults
class RecyclerPlatHolder(itemView: View?)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var txtTitle: TextView = itemView!!.findViewById(R.id.texttitle)
    var txtaddress: TextView = itemView!!.findViewById(R.id.textdir)
    var relative :RelativeLayout = itemView!!.findViewById(R.id.contentcasilla)
    var logo:ImageView = itemView!!.findViewById(R.id.logo)
    init {
        itemView!!.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
    }

}

   class RecyclerAdapter(private val item: RealmResults<JsonFeedBd>?,
                         private val mContext: Activity?, private val firstTime:Boolean?)
    : RecyclerView.Adapter<RecyclerPlatHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerPlatHolder {
        val itemView = inflater.inflate(R.layout.item_recycler, parent, false)
        return RecyclerPlatHolder(itemView)
    }

    override fun getItemCount(): Int {
        //validate first time when show 10 objects
        return if(firstTime==true){
            if(item!!.size>=10){
                (mContext as MainActivity).firstime=false
                10
            } else{
                item.size
            }
        }
        else{
            item!!.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerPlatHolder, index: Int) {
        try{
            item
            //fill list
            holder.txtTitle.text = item!![index]!!.team_name
            holder.txtaddress.text = item[index]!!.address
            //load images via glide
            CodeChallenge.instance.variablesGlobales!!.backgroundUrl(mContext!!,item[index]!!.img_logo,holder.logo)
            holder.relative.setOnClickListener({
                if((mContext as MainActivity).mislandscape){
                    mContext.resultadoRealmporId(index+1)
                    if(mContext.mresultrealm!!.size>0){
                        mContext.fragmentHome()
                    }
                }
                else{
                    //call of detail activity
                    CodeChallenge.instance.variablesGlobales!!.gotoActvity(mContext, DetailActivity::class.
                            java,item[index]!!.id.toString(),0)
                }
            })
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

}