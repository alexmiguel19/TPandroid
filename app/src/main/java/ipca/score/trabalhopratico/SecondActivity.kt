package ipca.score.trabalhopratico

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {

    var CITY: String = ""
    val API: String = "604fe09d5e30777dd58763919056e8d0"
    var temp: String=" "
    var weatherDescription: String= " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        CITY = intent.getStringExtra(NAME_EXTRA)


        weatherTask().execute()
    }


    companion object{
        val NAME_EXTRA = "ipca.score.trabalhopratico.SecondActivity.name"
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

        //aqui iremos buscar informaçao a net neste caso usando o nome da cidade
        override fun doInBackground(vararg params: String?): String? {
            var response:String?

                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            return response
        }

        //por fim aqui podemos dar update a interface
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // recebemos a informaçã0 do site (result) e pegamos nela e metemos nos nossos views com a utilização de jsonObj
            //ja que a informação de nos é dade tem de ser transformada para podermos usar de maneira adequada


                val jsonObj = JSONObject(result)
                val mainInf = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                  temp = mainInf.getString("temp")+"°C"
                val tempMin = "Min Temp: " + mainInf.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + mainInf.getString("temp_max")+"°C"
                val pressure = mainInf.getString("pressure")
                val humidity = mainInf.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                 weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")


                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise*1000)
                )
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset*1000)
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, CITY+" "+ temp+ " " + weatherDescription)
                startActivity(Intent.createChooser(shareIntent, "Partilhar"))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
