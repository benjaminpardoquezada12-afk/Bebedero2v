package com.example.bebedero2v

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.firebase.database.FirebaseDatabase
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

        btnEncender.setOnClickListener {
            tvEstadoMotor.text = "Motor 12v: Encendido"
            tvEstadoMotor.setTextColor("#28A745".toColorInt())
            Toast.makeText(this, "Bomba Encendida", Toast.LENGTH_SHORT).show()
        }

        btnApagar.setOnClickListener {
            tvEstadoMotor.text = "Motor 12v: Apagado"
            tvEstadoMotor.setTextColor("#DC3545".toColorInt())
            Toast.makeText(this, "Bomba Apagada", Toast.LENGTH_SHORT).show()

            guardarConsumoEnFirebase(12.3)
        }

        btnIrHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    @Suppress("SetTextI18n")
    private fun guardarConsumoEnFirebase(litros: Double) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formato.format(Date())

        val registro = HashMap<String, Any>()
        registro["fecha"] = fechaActual
        registro["litros"] = litros

        database.child("historial_consumo").push().setValue(registro)
            .addOnSuccessListener {
                Toast.makeText(this, "Consumo guardado en la base de datos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show()
            }
    }
}