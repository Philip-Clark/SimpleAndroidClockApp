package biff.project


import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL




class WeatherHttpClient {

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?"
    }

    fun getWeatherData(lat: String,lon : String): String? {



        var con: HttpURLConnection? = null
        var `is`: InputStream? = null
        try {
            con = URL("$BASE_URL&lon=$lon&lat=$lat&units=imperial&APPID=654a9571667134ff7f60cad1520fa93e").openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.doInput = true
            con.doOutput = true
            con.connect()


            // Let's read the response
            val buffer = StringBuffer()
            `is` = con.inputStream
            val br = BufferedReader(InputStreamReader(`is`))
            var line: String?
            while (br.readLine().also { line = it } != null) buffer.append(
                """
    $line
    
    """.trimIndent()
            )
            `is`.close()
            con.disconnect()
            return buffer.toString()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            try {
                `is`!!.close()
            } catch (t: Throwable) {
            }
            try {
                con!!.disconnect()
            } catch (t: Throwable) {
            }
        }
        return null
    }



}