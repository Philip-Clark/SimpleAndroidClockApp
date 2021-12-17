@file:Suppress("RemoveExplicitTypeArguments")

package biff.project

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import biff.project.R.*
import biff.project.R.drawable.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class MainActivity : AppCompatActivity() {


    private var started = false
    private val handler = Handler()
    private var lastH : Float = 0F
    private var lastM : Float = 0F
    private var lastS : Float = 0F

    private var currentTime: Calendar = Calendar.getInstance()
    private var t = currentTime.time
    private var day = currentTime.get(Calendar.DAY_OF_WEEK)-1


    private var date =  SimpleDateFormat("MM.dd.yy").format(t).toString()
    private var days = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
    private var hour = ((SimpleDateFormat("HH").format(t)).toFloat())
    private var minute = ((SimpleDateFormat("mm").format(t)).toFloat())
    private var second = ((SimpleDateFormat("ss").format(t)).toFloat())
    private var timeFormatted: String = SimpleDateFormat("h:mm:ss").format(t)





    private var temp: TextView? = null
    private var imgView: ImageView? = null
    private var imgViewS: ImageView? = null


    private var weather : Weather? = null
    var city = ""
    private var longitude = (-90..90).random().toDouble()
    private var latitude = (-90..90).random().toDouble()



    private var switcher: TextSwitcher? = null

    private val locHandler = locationHandler()

    private val runnable = Runnable {
        val hourHand : ImageView = findViewById(id.HourHand)
        val minHand : ImageView = findViewById(id.MinuteHand)
        val secHand : ImageView =findViewById(id.SecondHand)



        currentTime = Calendar.getInstance()
        t = currentTime.time
        timeFormatted = SimpleDateFormat("h:mm:ss").format(t)

        

        updateTimes()
        
        switcher!!.setText(timeFormatted)
        
        if(hour != lastH){

            findViewById<TextView>(id.date).text = date
            findViewById<TextView>(id.day).text = days[day]
            animateHands(hourHand,(hour-lastH) * 30,ValueAnimator.ofFloat(hourHand.rotation),1F,900)
            lastH = hour
        }

        if(minute != lastM) {
            animateHands(minHand, (minute-lastM) * 6, ValueAnimator.ofFloat(minHand.rotation),1F,600)
            lastM = minute
        }

        if(second != lastS) {
            if(lastS == 59F && second == 0F) {
                animateHands(secHand, 6F, ValueAnimator.ofFloat(secHand.rotation), -2F, 100)
            }else{
                animateHands(secHand, (second - lastS) * 6, ValueAnimator.ofFloat(secHand.rotation), -2F, 100)

            }
            lastS = second

        }

        updateHands()
        if (started) {
            start(100)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {


        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val targetW = findViewById<ImageView>(id.Circle).layoutParams.width


        hour = 0F
        minute = 0F
        second = 0F



        findViewById<TextView>(id.date).text = date
        findViewById<TextView>(id.day).text = days[day]

        switcher = findViewById(id.Switcher)

        switcher!!.setInAnimation(this,anim.clock_in)
        switcher!!.setOutAnimation(this,anim.clock_out)

        val hourHand : ImageView = findViewById(id.HourHand)
        val minHand : ImageView = findViewById(id.MinuteHand)
        val secHand : ImageView =findViewById(id.SecondHand)

        hourHand.layoutParams.width = targetW
        hourHand.layoutParams.height = targetW
        hourHand.requestLayout()

        minHand.layoutParams.width = targetW
        minHand.layoutParams.height = targetW

        minHand.requestLayout()

        secHand.layoutParams.width = targetW
        secHand.layoutParams.height = targetW
        secHand.requestLayout()

        updateHands()

        temp = findViewById(id.temp)

        imgView = findViewById(id.weather)
        imgViewS = findViewById(id.weatherShadow)



        ActivityCompat.requestPermissions(this, (arrayOf({ Manifest.permission.ACCESS_FINE_LOCATION }.toString())), 1)




        updateWeather()



        findViewById<ImageView>(id.weather).setOnClickListener{ updateWeather() }


    }




    private fun animateHands(view: ImageView, rotationAmount: Float, animator: ValueAnimator, spring: Float, duration: Long)
    {
        val currentRotation = view.rotation
        animator.duration = duration
        animator.interpolator = OvershootInterpolator(spring)


        animator.addUpdateListener{ valAni ->
            val v = valAni.animatedFraction
            view.rotation = currentRotation + (rotationAmount * v)
        }


        animator.start()


    }


    private fun updateWeather()
    {

        locHandler.getWeatherAtLocation(this)
        longitude = locHandler.longitude
        latitude = locHandler.latitude
        val task = JSONWeatherTask()
        task.execute()

    }


    private fun updateTimes()
    {

        day = currentTime.get(Calendar.DAY_OF_WEEK)-1
        date =  SimpleDateFormat("MM-dd-yy").format(t).toString()
        hour = ((SimpleDateFormat("HH").format(t)).toFloat())
        minute = ((SimpleDateFormat("mm").format(t)).toFloat())
        second = ((SimpleDateFormat("ss").format(t)).toFloat())
    }

    private fun updateHands()
    {

        updateTimes()

        lastH = hour
        lastM = minute
        lastS = second

        findViewById<ImageView>(id.HourHand).rotation = (hour * 30)
        findViewById<ImageView>(id.MinuteHand).rotation = (minute * 6)
        findViewById<ImageView>(id.SecondHand).rotation = (second * 6)

    }


    override fun onPause()
    {

        super.onPause()
        stop()

    }


    override fun onResume()
    {

        updateHands()
        super.onResume()
        updateWeather()
        start(10)

    }



    private fun stop()
    {

        started = false
        handler.removeCallbacks(runnable)

    }


    private fun start(t : Long)
    {

        started = true
        handler.postDelayed(runnable, t)

    }




    @SuppressLint("StaticFieldLeak")
    private inner class JSONWeatherTask : AsyncTask<String?, Void?, Weather>()
    {

        override fun doInBackground(vararg p0: String?): Weather?
        {

            weather = Weather()

            val data = WeatherHttpClient().getWeatherData(latitude.toString(),longitude.toString())

            try {
                weather = JSONWeatherParser.getWeather(data,this@MainActivity)

            } catch (e: JSONException) {
                e.printStackTrace()
            }catch (e: NullPointerException){
                print("Error : NullPointer")
            }
            return weather
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(weather: Weather)
        {
            super.onPostExecute(weather)
            try {
                city = weather.location!!.city.toString()
                displayIcon(weather.icon.toString())

                findViewById<TextView>(id.windspeed)!!.text = weather.windspeed!!.toString() + "mph"
                findViewById<TextView>(id.winddir)!!.text = weather.winddir!!
                findViewById<TextView>(id.pressr)!!.text = weather.pressr!!.toString()

                temp!!.text = "" + weather.temp.toInt().toString() + "°"
            }catch (e: java.lang.NullPointerException){
                print("Error")



                findViewById<TextView>(id.windspeed)!!.alpha = 0.0f
                findViewById<TextView>(id.winddir)!!.alpha = 0.0f
                findViewById<TextView>(id.pressr)!!.alpha = 0.0f
                imgView!!.alpha = 0.0f
                imgViewS!!.alpha = 0.0f
                temp!!.alpha = 0.0f
                findViewById<ImageView>(id.divider).alpha = 0.0f

                showErrorToast("API call limit overdrawn \nWeather unavailable")
            }
        }

    }

    fun showErrorToast(msg : String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    fun displayIcon(code : String)
    {

        var icon : Int = sun

        when (code.dropLast(1))
        {
            "01" -> icon = sun
            "02" -> icon = cloud_sun
            "03" -> icon = cloud_sun
            "04" -> icon = cloud
            "09" -> icon = rain_sun
            "10" -> icon = rain
            "11" -> icon = storm
            "13" -> icon = rain
            "50" -> icon = wind
        }

        imgView!!.setImageResource(icon)
        imgViewS!!.setImageResource(icon)

    }






}



