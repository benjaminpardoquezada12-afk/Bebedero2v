package com.example.bebedero2v

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance(
        "https://bebederoiot-default-rtdb.firebaseio.com/"
    ).reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvEstadoMotor = findViewById<TextView>(R.id.tvEstadoMotor)
        val btnEncender = findViewById<Button>(R.id.btnEncender)
        val btnApagar = findViewById<Button>(R.id.btnApagar)
        val btnIrHistorial = findViewById<Button>(R.id.btnIrHistorial)

        database.child("estado_bomba").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encendida = snapshot.getValue(Boolean::class.java) ?: false
                if (encendida) {
                    tvEstadoMotor.text = "Motor 12v: Encendido"
                    tvEstadoMotor.setTextColor("#28A745".toColorInt())
                } else {
                    tvEstadoMotor.text = "Motor 12v: Apagado"
                    tvEstadoMotor.setTextColor("#FF00BF".toColorInt())
                }
                if (encendida == true) {
                    btnEncender.isEnabled = false
                    btnApagar.isEnabled = true
                } else {
                    btnEncender.isEnabled = true
                    btnApagar.isEnabled = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Error de sincronización", Toast.LENGTH_SHORT).show()
            }
        })

        btnEncender.setOnClickListener {
            if (!estaConectadoAWiFi()) {
                Toast.makeText(this, "Acceso denegado: Debes conectarte a la red Wi-Fi del Bebedero", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            database.child("estado_bomba").setValue(true)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bomba Encendida vía Wi-Fi", Toast.LENGTH_SHORT).show()
                }
        }

        btnApagar.setOnClickListener {
            if (!estaConectadoAWiFi()) {
                Toast.makeText(this, "Acceso denegado: Debes conectarte a la red Wi-Fi del Bebedero", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            database.child("estado_bomba").setValue(false)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bomba Apagada vía Wi-Fi", Toast.LENGTH_SHORT).show()
                }
            val consumoAleatorio = String.format(java.util.Locale.US,"%.1f",kotlin.random.Random.nextDouble(10.0 , 25.0)).toDouble()
            guardarConsumoEnFirebase(litros =   consumoAleatorio)
        }

        btnIrHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    private fun estaConectadoAWiFi(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    @Suppress("SetTextI18n")
    private fun guardarConsumoEnFirebase(litros: Double) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formato.format(Date())

        val registro = HashMap<String, Any>()
        registro["fecha"] = fechaActual
        registro["litros"] = litros

        database.child("historial_consumo").push().setValue(registro)
    }
}