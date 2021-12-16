package biff.project

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import biff.project.MainActivity

class locationHandler {



    private var locationManager : LocationManager? = null
    var longitude = 0.0
    var latitude = 0.0



    fun getWeatherAtLocation(context: AppCompatActivity){

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER))
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

        builder.setNegativeButton("No Thanks!", DialogInterface.OnClickListener
        {
                dialog, which -> dialog.cancel()
        })

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setTitle("Allow GPS Permissions")
        alertDialog.show()
    }

    private fun getLocation(context:AppCompatActivity)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }

        else
        {
            val locationGPS: Location? =
                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (locationGPS != null)
            {
                val lat: Double = locationGPS.latitude
                val long: Double = locationGPS.longitude
                latitude = lat
                longitude = long
            }


            else
            {

                Toast.makeText(context, "Unable to find location.", Toast.LENGTH_SHORT).show()

                if(locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, locationListenerGps)
                }
                if(locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                {
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, locationListenerNetwork)
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
            latitude = location.latitude

        }
    }
    abstract class LocationResult
    {

        abstract fun gotLocation(location: Location?)

    }
}

