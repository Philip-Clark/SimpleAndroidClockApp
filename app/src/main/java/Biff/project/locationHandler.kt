package biff.project

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*

class locationHandler {



    private var locationManager : LocationManager? = null
    var longitude = 0.0
    var latitude = 0.0
    var pressr = 0.00F
    var lastLocation : SharedPreferences? = null



    fun getWeatherAtLocation(context: AppCompatActivity){
        lastLocation = context.getSharedPreferences("location",0)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            &&
            lastLocation!!.getFloat("long",999F) == 999F)
        {
            OnGPS(context)
        }
        else
        {
            getLocation(context)

        }
    }




    private fun OnGPS(context : AppCompatActivity)
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Enable GPS for Local weather?")
        builder.setCancelable(false)

        builder.setPositiveButton("Sure", DialogInterface.OnClickListener
        {
                dialog, which -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        })

        builder.setNegativeButton("Not now", DialogInterface.OnClickListener
        {
                dialog, which -> dialog.cancel()

        })


        val alertDialog: AlertDialog = builder.create()
        alertDialog.setTitle("Enable Location")
        alertDialog.show()
    }

    private fun getLocation(context:AppCompatActivity)
    {
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED
        ) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED) {
                requestPermissions(context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
                requestPermissions(context,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1)

            } else {
                val locationGPS: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (locationGPS != null) {
                    val lat: Double = locationGPS.latitude
                    val long: Double = locationGPS.longitude
                    latitude = lat
                    longitude = long
                    lastLocation!!.edit().putFloat("lon",long.toFloat()).putFloat("lat",lat.toFloat()).commit()
//                    Toast.makeText(context,"saved location",Toast.LENGTH_LONG).show()

                } else {

                    if(lastLocation!!.contains("lon") && lastLocation!!.contains("lat")) {
                        longitude = lastLocation!!.getFloat("lon", 0.00F).toDouble()
                        latitude = lastLocation!!.getFloat("lat", 0.00F).toDouble()
//                        Toast.makeText(context,"got saved location",Toast.LENGTH_SHORT).show()
                    }else {

//                        Toast.makeText(context, "Unable to retrieve local weather", Toast.LENGTH_SHORT).show()

                        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0,
                                0F,
                                locationListenerGps)
                        }
                        if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                0,
                                0F,
                                locationListenerNetwork)
                        }
                    }


                }
            }
        } else {
            val locationGPS: Location? =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (locationGPS != null) {
                val lat: Double = locationGPS.latitude
                val long: Double = locationGPS.longitude
                latitude = lat
                longitude = long
//                lastLocation!!.edit().putFloat("lon",long.toFloat()).putFloat("lat",lat.toFloat()).commit()
//                Toast.makeText(context,"saved location",Toast.LENGTH_LONG).show()

            } else {

                if(lastLocation!!.contains("lon") && lastLocation!!.contains("lat")) {
                    longitude = lastLocation!!.getFloat("lon", 0.00F).toDouble()
                    latitude = lastLocation!!.getFloat("lat", 0.00F).toDouble()
//                    Toast.makeText(context,"got saved location",Toast.LENGTH_SHORT).show()
                }else {

//                    Toast.makeText(context, "Unable to retrieve local weather", Toast.LENGTH_SHORT).show()

                    if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0,
                            0F,
                            locationListenerGps)
                    }
                    if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            0,
                            0F,
                            locationListenerNetwork)
                    }
                }


            }
        }
    }


    var locationListenerGps: LocationListener = object : LocationListener
    {
        override fun onLocationChanged(p0: Location)
        {
            locationResult.gotLocation(p0)
        }
    }

    var locationListenerNetwork: LocationListener = object : LocationListener
    {
        override fun onLocationChanged(p0: Location)
        {
            locationResult.gotLocation(p0)
        }
    }

    var locationResult = object : LocationResult()
    {
        override fun gotLocation(location: Location?)
        {

            longitude = location!!.longitude
            latitude = location!!.latitude

        }
    }
    abstract class LocationResult
    {

        abstract fun gotLocation(location: Location?)

    }
}

