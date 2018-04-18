package com.challenge.applaudo.codechallenge.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.api.CodeChallenge

//fragment for home
class Fragment_Home : Fragment() {
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_header, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHeader()

    }
    fun setHeader() {
        try {
            val home = rootView!!.findViewById<ImageView>(R.id.icon_home)
            home.setOnClickListener {
                try {
                    Toast.makeText(context, "home", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
            }

            try{
                val phoneclick = rootView!!.findViewById<ImageView>(R.id.icon_call)
                phoneclick.setOnClickListener {
                    try {
                       CodeChallenge.instance.variablesGlobales!!.makeCall(activity, phone)

                    } catch (e: Exception) {
                    }
                }

                val shareclick = rootView!!.findViewById<ImageView>(R.id.icon_share)
                shareclick.setOnClickListener {
                    try {
                        CodeChallenge.instance.variablesGlobales!!.shareContent(activity, share)

                    } catch (e: Exception) {
                    }
                }

            }
            catch (e:Exception){}


        } catch (e: Exception) {
        }
    }
    companion object {
        private var phone:String =""
        private var share:String=""
        fun getInstance(_phone:String?,_share:String?): Fragment {
            val fragment = Fragment_Home()
            try {
                phone = _phone!!
                share=_share!!

            } catch (e: Exception) {
            }

            return fragment
        }
    }

}