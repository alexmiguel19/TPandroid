package ipca.score.trabalhopratico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ThirdActivity : AppCompatActivity() {


    var LAT: String = ""
    var LON: String = ""
    val API: String = "604fe09d5e30777dd58763919056e8d0"
    var temp: String =" "
    var CITY: String = " "
    var weatherDescription: String= " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        LAT = intent.getStringExtra(NAME_EXTRA)
        LON = intent.getStringExtra(NAME2_EXTRA)


        weatherTask().execute()
    }


    companion object {
        val NAME_EXTRA = "ipca.score.trabalhopratico.ThirdActivity.name"
        val NAME2_EXTRA = "ipca.score.trabalhopratico.ThirdActivity.name2"

    }
    //Usamos uma coisa chamada AsyncTask que torna mais facil a utilização do user interface podendo fazer coisas no backgroud
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        //aqui iremos mostrar algo ao utilizador para ele saber que a app esta a ir buscar informação a net
        override fun onPreExecute() {
            super.onPreExecute()

            //pomos uma roda a rodar para mustrar que ta a "pensar"
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        //aqui iremos buscar informaçao a net neste caso usando a latitude e a longitude
        override fun doInBackground(vararg params: String?): String? {
            var response: String?

                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LON&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // recebemos a informaçã0 do site (result) e pegamos nela e metemos nos nossos views com a utilização de jsonObj
            //ja que a informação de nos é dade tem de ser transformada para podermos usar de maneira adequada


            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
             temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
             weatherDescription = weather.getString("description")

            val address = jsonObj.getString("name") + ", " + sys.getString("country")
            CITY=jsonObj.getString("name")



            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.updated_at).text = updatedAtText
            findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<TextView>(R.id.temp_min).text = tempMin
            findViewById<TextView>(R.id.temp_max).text = tempMax
            findViewById<TextView>(R.id.sunrise).text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise * 1000)
                )
            findViewById<TextView>(R.id.sunset).text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset * 1000)
                )
            findViewById<TextView>(R.id.wind).text = windSpeed
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity

            //ja que a informação esta nos views escondemos o loador w mostramos o as views todas que estão dentro do Container
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE


        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {

                val shareIntent: Intent
                shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, CITY+" "+ temp+" "+ weatherDescription)
                startActivity(Intent.createChooser(shareIntent, "Partilhar"))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

