package ipca.score.trabalhopratico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_first.*


class FirstActivity : AppCompatActivity()  {

    //Esta activity é responsavel por obter as coordenadas gps do telemovel bem como da las ao utiliazdor.
    //Tambem apresenta a escolha da cidade para a previsão.



    val TAG_ACTIVITY : String = " "
    var editTextName : EditText? = null


    val TAG_ACTIVITY2: String= " "
    var editNumberCoordLON: EditText? = null
    var editNumberCoordLAT: EditText? = null


    //ajuda a identificar a ação da pessoa pode tomar qualquer valor
    val PERMISSION_ID = 0
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()


        //Esta editTextName ira receber o noma de cidade que escrevemos.
        editTextName = findViewById(R.id.editTextName)

        //estas as coordenadas que cremos latitude e longitude respectivamente
        editNumberCoordLAT=findViewById(R.id.editTextCoord2)
        editNumberCoordLON=findViewById(R.id.editTextCoord1)

        //Ao clicarmos no primeiro botão iremos mandar a imformação escrita para a proxima activity
        //Iemos passar tambem para a activity responsavel por fazer a procura do tempo pelo nome da cidade nesta caso a SecondActivity
        buttonOk.setOnClickListener{
            Log.d(TAG_ACTIVITY, editTextName!!.text.toString() )
            val intent = Intent(this@FirstActivity, SecondActivity::class.java)
            intent.putExtra(SecondActivity.NAME_EXTRA, editTextName!!.text.toString()  )
            startActivity(intent)
        }

        //Ao clicarmos no primeiro botão iremos mandar a imformação escrita para a proxima activity
        //Iemos passar tambem para a activity responsavel por fazer a procura do tempo pelas coordenadas nesta caso a ThirdActivity
        buttonOk2.setOnClickListener {
            Log.d(TAG_ACTIVITY2, editNumberCoordLAT!!.text.toString())
            Log.d(TAG_ACTIVITY2, editNumberCoordLON!!.text.toString())

            val intent = Intent(this@FirstActivity, ThirdActivity::class.java)
            intent.putExtra(ThirdActivity.NAME_EXTRA, editNumberCoordLAT!!.text.toString() )
            intent.putExtra(ThirdActivity.NAME2_EXTRA, editNumberCoordLON!!.text.toString() )

            startActivity(intent)


        }


    }


    //usa o api do telemovel e da nos as coordenadas
    //primeiro verificamos se temos assesso e se o gps ta ligado
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    //Em alguns casos raros a localização pode ser null
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        //pedimos uma nova localização
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
    //depois de receber a informação metemos la nos text views
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    //isto serve para ver se o gps do telemovel ta ativado porque pode ser dado a permissão e o usar mas este não tar ligado
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //responsavel por nos dizer se o utilizador deu nos permissão de usar a localização do  telemovel
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    //responsavel por pedir permissão se ja não a tivermos
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


}