package com.challenge.applaudo.codechallenge.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.challenge.applaudo.codechallenge.R
import com.challenge.applaudo.codechallenge.api.CodeChallenge

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //initialize
        try{
            //set text font
            textsplash.typeface = CodeChallenge.instance.arialBold
            //set animation circular
            val anim1 = AnimationUtils.loadAnimation(this, R.anim.anim_bot)
            val img = findViewById<View>(R.id.imageView) as ImageView
            CodeChallenge.instance.variablesGlobales!!.
                    glideback(this,R.drawable.androidimage,img)
            CodeChallenge.instance.variablesGlobales!!.glideback(
                    this,R.drawable.code,mfondo)

            img.animation = anim1
            val anim = ObjectAnimator.ofInt(mBarcircle, "progress", 0, 100)
            anim.duration = 3000
            anim.interpolator = DecelerateInterpolator()
            anim.start()
            //handler to view splash for 3 seconds
            val handler = Handler()
            handler.postDelayed({
               CodeChallenge.instance.variablesGlobales!!.gotoActvity(this,MainActivity::class.java,
                       "",1)
                finish()
            }, 2550)
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

    //funciones supervisoras de la memoria
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }
}
