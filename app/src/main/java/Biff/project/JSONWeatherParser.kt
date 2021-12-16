/**
 * This is a tutorial source code
 * provided "as is" and without warranties.
 *
 * For any question please visit the web site
 * http://www.survivingwithandroid.com
 *
 * or write an email to
 * survivingwithandroid@gmail.com
 *
 */

import org.json.JSONException
import org.json.JSONObject

/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public object JSONWeatherParser {
    @Throws(JSONException::class)
    fun getWeather(data: String?): Weather {
        val weather = Weather()

        // We create out JSONObject from the data
        val jObj = JSONObject(data)

        // We start extracting the info
        val loc = Location()
        val coordObj = getObject("coord", jObj)
        loc.latitude = getFloat("lat", coordObj)
        loc.longitude = getFloat("lon", coordObj)
        val sysObj = getObject("sys", jObj)
        loc.sunrise = getInt("sunrise", sysObj).toLong()
        loc.sunset = getInt("sunset", sysObj).toLong()
        loc.city = getString("name", jObj)
        weather.location = loc

        // We get weather info (This is an array)
        val jArr = jObj.getJSONArray("weather")

        // We use only the first value
        val JSONWeather = jArr.getJSONObject(0)
        weather.currentCondition.weatherId = getInt("id", JSONWeather)
        weather.currentCondition.descr = getString("description", JSONWeather)
        weather.currentCondition.condition = getString("main", JSONWeather)
        weather.currentCondition.icon = getString("icon", JSONWeather)
        val mainObj = getObject("main", jObj)
        weather.currentCondition.humidity = getInt("humidity", mainObj).toFloat()
        weather.currentCondition.pressure = getInt("pressure", mainObj).toFloat()
        weather.temperature.maxTemp = getFloat("temp_max", mainObj)
        weather.temperature.minTemp = getFloat("temp_min", mainObj)
        weather.temperature.temp = getFloat("temp", mainObj)

        // Wind
        val wObj = getObject("wind", jObj)
        weather.wind.speed = getFloat("speed", wObj)
        weather.wind.deg = getFloat("deg", wObj)

        // Clouds
        val cObj = getObject("clouds", jObj)
        weather.clouds.perc = getInt("all", cObj)

        // We download the icon to show
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
        return jObj.getInt(tagName)
    }
}