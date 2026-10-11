package com.example.bebedero2v

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    // Conexión a Firebase Realtime Database
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
        btnIrHistorial.setOnClickListener {
            val intent = android.content.Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
        // Control visual local del motor
        btnEncender.setOnClickListener {
            tvEstadoMotor.text = "Motor 12v: Encendido"
            tvEstadoMotor.setTextColor(Color.parseColor("#28A745"))
            Toast.makeText( this,"Bomba Encendida", Toast.LENGTH_SHORT).show()
        }

        btnApagar.setOnClickListener {
            tvEstadoMotor.text = "Motor 12v: Apagado"
            tvEstadoMotor.setTextColor(Color.parseColor("#DC3545"))
            Toast.makeText(this, "Bomba Apagada", Toast.LENGTH_SHORT).show()

            // Al apagar, simulamos el fin de llenado y registramos el consumo en Firebase
            guardarConsumoEnFirebase(12.3)
        }
    }

    // Función para guardar el registro de litros y fecha en Firebase
    private fun guardarConsumoEnFirebase(litros: Double) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formato.format(Date())

        val registro = HashMap<String, Any>()
        registro["fecha"] = fechaActual
        registro["litros"] = litros

        // Sube los datos al nodo "historial_consumo"
        database.child("historial_consumo").push().setValue(registro)
            .addOnSuccessListener {
                Toast.makeText(this, "Consumo guardado en la base de datos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show()
            }
    }
}