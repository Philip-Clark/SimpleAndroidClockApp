package biff.project


import org.json.JSONException
import org.json.JSONObject

object JSONWeatherParser {
    @Throws(JSONException::class)
    fun getWeather(data: String?): Weather {
        val weather = Weather()

        // We create out JSONObject from the data
        val jObj = JSONObject(data)

        // We start extracting the info
        val loc = Location()
        loc.city = getString("name", jObj)
        weather.location = loc

        val jArr = jObj.getJSONArray("weather")

        val JSONWeather = jArr.getJSONObject(0)
        weather.icon = getString("icon", JSONWeather)
        val mainObj = getObject("main", jObj)
        weather.temp = getFloat("feels_like", mainObj)

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
}