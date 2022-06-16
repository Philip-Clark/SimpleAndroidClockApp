package biff.project


import android.content.Context
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

object JSONWeatherParser {
    @Throws(JSONException::class)
    fun getWeather(data: String?,contex : Context): Weather {
        val weather = Weather()

        // We create out JSONObject from the data
        if(JSONObject(data) != null) {
            val jObj = JSONObject(data)
            print(data)
            // We start extracting the info
            val loc = Location()
            loc.city = getString("name", jObj)
            weather.location = loc

            val jArr = jObj.getJSONArray("weather")

            val JSONWeather = jArr.getJSONObject(0)
            weather.icon = getString("icon", JSONWeather)
            val mainObj = getObject("main", jObj)
            weather.temp = getFloat("temp", mainObj)

            val windObj = getObject("wind", jObj)
            val winddir = getInt("deg", windObj)
            weather.windspeed = getFloat("speed", windObj).toInt()


            val compassSector = arrayOf("N",
                "NNE",
                "NE",
                "ENE",
                "E",
                "ESE",
                "SE",
                "SSE",
                "S",
                "SSW",
                "SW",
                "WSW",
                "W",
                "WNW",
                "NW",
                "NNW",
                "N")
            weather.winddir = compassSector[((winddir.toFloat() / 22.5).toInt())]

            weather.pressr =
                ((getFloat("pressure", mainObj) * 0.030F) * 100).toInt().toFloat() / 100


        }else{
            Toast.makeText(contex,"ERROR PARSING JSON",Toast.LENGTH_LONG)

        }

        return weather
    }

    @Throws(JSONException::class)
    private fun getObject(tagName: String, jObj: JSONObject): JSONObject {
        return jObj.getJSONObject(tagName)
    }

    @Throws(JSONException::class)
    private fun getString(tagName: String, jObj: JSONObject): String {
        return jObj.getString(tagName)
    }

    @Throws(JSONException::class)
    private fun getFloat(tagName: String, jObj: JSONObject): Float {
        return jObj.getDouble(tagName).toFloat()
    }

    @Throws(JSONException::class)
    private fun getInt(tagName: String, jObj: JSONObject): Int {
        return jObj.getDouble(tagName).toInt()
    }
}