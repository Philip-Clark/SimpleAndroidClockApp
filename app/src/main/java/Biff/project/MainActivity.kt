package biff.project
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextSwitcher
import android.R

import android.media.MediaPlayer
import android.view.View
import androidx.fragment.app.FragmentActivity


class MainActivity : AppCompatActivity() {


    private var started = false
    private val handler = Handler()
    private var format = SimpleDateFormat("h:mm:ss")
    private var lastH : Float = 0F;
    private var lastM : Float = 0F;
    private var lastS : Float = 0F;

    var currentTime: Calendar = Calendar.getInstance()
    var t = currentTime.time

    var d = SimpleDateFormat("MM-dd-yy")
    var date : String =  d.format(t).toString()

    var day = currentTime.get(Calendar.DAY_OF_WEEK)-1
    var days = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")

    var hour = SimpleDateFormat("HH")
    var H : Float = ((hour.format(t)).toFloat()).toFloat()

    var min = SimpleDateFormat("mm")
    var M : Float = ((min.format(t)).toFloat()).toFloat()

    var sec = SimpleDateFormat("ss")
    var S : Float = ((sec.format(t)).toFloat()).toFloat()

    var timeFormatted: String = format.format(t)

    var mp: MediaPlayer = MediaPlayer()



    var newTime = timeFormatted
    var oldTime: String = ""
    private val runnable = Runnable {
        var hourHand : ImageView = findViewById<ImageView>(biff.project.R.id.HourHand)
        var minHand : ImageView = findViewById<ImageView>(biff.project.R.id.MinuteHand)
        var secHand : ImageView =findViewById<ImageView>(biff.project.R.id.SecondHand)



        currentTime = Calendar.getInstance()
        t = currentTime.time
        timeFormatted = format.format(t)

        val switcher = findViewById<TextSwitcher>(biff.project.R.id.Switcher)

        switcher.setInAnimation(this,biff.project.R.anim.clock_in)
        switcher.setOutAnimation(this,biff.project.R.anim.clock_out)




        switcher.setText(timeFormatted)



        updateTimes()







        if(H != lastH){

            findViewById<TextView>(biff.project.R.id.date).text = date
            findViewById<TextView>(biff.project.R.id.day).text = days[day].toString()
            animateHands(hourHand,(H-lastH) * 30,ValueAnimator.ofFloat(hourHand.rotation),1F,900)
            lastH = H
        }

        if(M != lastM) {

            animateHands(minHand, (M-lastM) * 6, ValueAnimator.ofFloat(minHand.rotation),1F,600)
            lastM = M
        }

        if(S != lastS) {
//            mp.start()
            if(lastS == 59F && S == 0F) {
                animateHands(secHand, 6F, ValueAnimator.ofFloat(secHand.rotation), -2F, 100)
            }else{
                animateHands(secHand, (S - lastS) * 6, ValueAnimator.ofFloat(secHand.rotation), -2F, 100)

            }
            lastS = S

        }






        updateHands()
        if (started) {
            start(100)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(biff.project.R.layout.activity_main)

        val targetW = findViewById<ImageView>(biff.project.R.id.Circle).getLayoutParams().width

        mp = MediaPlayer.create(this,biff.project.R.raw.sound)
        mp.setVolume(0.8F,0.8F)

        H = 0F
        M = 0F
        S = 0F

        findViewById<TextView>(biff.project.R.id.date).text = date.toString()
        findViewById<TextView>(biff.project.R.id.day).text = days[day].toString()



        var hourHand : ImageView = findViewById<ImageView>(biff.project.R.id.HourHand)
        var minHand : ImageView = findViewById<ImageView>(biff.project.R.id.MinuteHand)
        var secHand : ImageView =findViewById<ImageView>(biff.project.R.id.SecondHand)

        hourHand.getLayoutParams().width = targetW
        hourHand.getLayoutParams().height = targetW
        hourHand.requestLayout()

        minHand.getLayoutParams().width = targetW
        minHand.getLayoutParams().height = targetW

        minHand.requestLayout();

        secHand.getLayoutParams().width = targetW
        secHand.getLayoutParams().height = targetW
        secHand.requestLayout();

        updateHands()

    }

    private fun animateHands(view: ImageView, increment: Float, animator: ValueAnimator, spring: Float, duration: Long){
        var SR = view.rotation
        animator.setDuration(duration)
        animator.interpolator = OvershootInterpolator(spring)

        animator.addUpdateListener { valAni ->

            val v = valAni.animatedFraction
            view.rotation = SR + (increment * v)

        }
        animator.start()
    }




    private fun updateTimes(){


        date = d.format(t).toString()
        day = currentTime.get(Calendar.DAY_OF_WEEK)-1
        H = ((hour.format(t)).toFloat()).toFloat()
        M = ((min.format(t)).toFloat()).toFloat()
        S = ((sec.format(t)).toFloat()).toFloat()
    }

    private fun updateHands(){


        updateTimes()
        lastH = H
        lastM = M
        lastS = S

        findViewById<ImageView>(biff.project.R.id.HourHand).rotation = (H * 30)
        findViewById<ImageView>(biff.project.R.id.MinuteHand).rotation = (M * 6)
        findViewById<ImageView>(biff.project.R.id.SecondHand).rotation = (S * 6)

    }

    override fun onPause() {
        super.onPause()
        stop()
        mp.release()
    }

    override fun onResume() {
        updateHands()
        super.onResume()
        start(10)
    }



    private fun stop() {
        started = false
        handler.removeCallbacks(runnable)
    }

    private fun start(t : Long) {
        started = true
        handler.postDelayed(runnable, t)
    }




}

