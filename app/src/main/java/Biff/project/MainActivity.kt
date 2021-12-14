package biff.project
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {


    private var started = false
    private val handler = Handler()
    private var format = SimpleDateFormat("h:mm:ss")
    private var lastH : Float = 0F;
    private var lastM : Float = 0F;
    private var lastS : Float = 0F;

    var currentTime: Calendar = Calendar.getInstance()
    var t = currentTime.time

    var hour = SimpleDateFormat("HH")
    var H : Float = ((hour.format(t)).toFloat()).toFloat()

    var min = SimpleDateFormat("mm")
    var M : Float = ((min.format(t)).toFloat()).toFloat()

    var sec = SimpleDateFormat("ss")
    var S : Float = ((sec.format(t)).toFloat()).toFloat()

    private val runnable = Runnable {
        var hourHand : ImageView = findViewById<ImageView>(biff.project.R.id.HourHand)
        var minHand : ImageView = findViewById<ImageView>(biff.project.R.id.MinuteHand)
        var secHand : ImageView =findViewById<ImageView>(biff.project.R.id.SecondHand)



        currentTime = Calendar.getInstance()
        t = currentTime.time
        var timeFormatted: String = format.format(t)
        findViewById<TextView>(biff.project.R.id.clock).text = timeFormatted

        updateTimes()


        if(H > lastH){
            lastH = H

            animateHands(hourHand,30,ValueAnimator.ofFloat(hourHand.rotation),3F,900)
        }

        if(M != lastM) {
            lastM = M

            animateHands(minHand, 6, ValueAnimator.ofFloat(minHand.rotation),2F,600)
        }

        if(S != lastS) {
            lastS = S
            animateHands(secHand,6,ValueAnimator.ofFloat(secHand.rotation),3F,50)
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

        H = 0F
        M = 0F
        S = 0F

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

    private fun animateHands(view: ImageView , increment : Int, animator : ValueAnimator, spring : Float, duration : Long){
        var SR = view.rotation
        animator.setDuration(duration)
        animator.interpolator = OvershootInterpolator(spring)

        animator.addUpdateListener { valAni ->

            val v = valAni.animatedFraction
            view.rotation = (SR + (increment * v))

        }
        animator.start()
    }

    private fun updateTimes(){
        H = ((hour.format(t)).toFloat()).toFloat()
        M = ((min.format(t)).toFloat()).toFloat()
        S = ((sec.format(t)).toFloat()).toFloat()
    }

    private fun updateHands(){



        updateTimes()
        findViewById<ImageView>(biff.project.R.id.HourHand).rotation = (H * 30)
        findViewById<ImageView>(biff.project.R.id.MinuteHand).rotation = (M * 6)
        findViewById<ImageView>(biff.project.R.id.SecondHand).rotation = (S * 6)
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onResume() {
        super.onResume()
        updateHands()

        start(0)
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


