package ipca.score.trabalhopratico



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()  {

    val TAG_ACTIVITY : String = " "

    //Criacao de uma "capa" para app com um botão para iniciar.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Start.setOnClickListener {
            //Ao carregar no botão iremos ser levados para a atividade seguinte (FirsActivity)
            val intent = Intent(this@MainActivity, FirstActivity::class.java)
            startActivity(intent)
        }
    }
}




