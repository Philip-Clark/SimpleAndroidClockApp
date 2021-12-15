package biff.project

import JSONWeatherParser
import Weather
import WeatherHttpClient
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*








public class MainActivity : AppCompatActivity() {


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




    private var cityText: TextView? = null
    private var condDescr: TextView? = null
    private var temp: TextView? = null
    private var press: TextView? = null
    private var windSpeed: TextView? = null
    private var windDeg: TextView? = null
    private var hum: TextView? = null
    private var imgView: ImageView? = null
    private var imgViewS: ImageView? = null



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


        val city = "London,UK"

//        condDescr = findViewById(R.id.condDescr) as TextView?
        temp = findViewById(biff.project.R.id.temp) as TextView?

        imgView = findViewById(biff.project.R.id.weather) as ImageView?
        imgViewS = findViewById(biff.project.R.id.weatherShadow) as ImageView?
        val task = JSONWeatherTask()
        task.execute("London,UK")

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




    private inner class JSONWeatherTask : AsyncTask<String?, Void?, Weather>() {
        protected override fun doInBackground(vararg p0: String?): Weather? {
            var weather = Weather()

            val data = WeatherHttpClient().getWeatherData("London,UK")
            println(data)

            try {
                weather = JSONWeatherParser.getWeather(data)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return weather
        }

        override fun onPostExecute(weather: Weather) {
            super.onPostExecute(weather)

            displayIcon(weather.currentCondition.icon.toString())

//            cityText!!.text = weather.location?.city + "," + weather.location?.country
//            condDescr!!.text =
//                weather.currentCondition.condition + "(" + weather.currentCondition.descr + ")"
            temp!!.text = "" + Math.round(weather.temperature.temp - 273.15) + "°C"
//            hum!!.text = "" + weather.currentCondition.humidity + "%"
//            press!!.text = "" + weather.currentCondition.pressure + " hPa"
//            windSpeed!!.text = "" + weather.wind.speed + " mps"
//            windDeg!!.text = "" + weather.wind.deg + "�"
        }
    }
fun displayIcon(code : String){
    var icon : Int = biff.project.R.drawable.sun
    
    when (code) {
        "01n" -> icon = biff.project.R.drawable.sun
        "01n" -> icon = biff.project.R.drawable.moon
        "02d" -> icon = R.drawable.cloud_sun
        "02n" -> icon = R.drawable.cloud_sun
        "03d" -> icon = R.drawable.cloud_sun
        "03n" -> icon = R.drawable.cloud_sun
        "04d" -> icon = R.drawable.cloud
        "04n" -> icon = R.drawable.cloud
        "09d" -> icon = R.drawable.rain_sun
        "09n" -> icon = R.drawable.rain_sun
        "10d" -> icon = R.drawable.rain
        "10n" -> icon = R.drawable.rain
        "11d" -> icon = R.drawable.storm
        "11n" -> icon = R.drawable.storm
        "13d" -> icon = R.drawable.rain
        "13n" -> icon = R.drawable.rain
        "50d" -> icon = R.drawable.wind
        "50n" -> icon = R.drawable.wind
        "unknown" -> icon = R.drawable.sun


    }
    imgView!!.setImageResource(icon)
    imgViewS!!.setImageResource(icon)

}
    






}


