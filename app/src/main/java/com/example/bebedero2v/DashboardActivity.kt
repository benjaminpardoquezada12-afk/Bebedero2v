package com.example.bebedero2v

import android.content.Intent
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
                    tvEstadoMotor.setTextColor("#DC3545".toColorInt())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Error de sincronización", Toast.LENGTH_SHORT).show()
            }
        })

        btnEncender.setOnClickListener {
            database.child("estado_bomba").setValue(true)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bomba Encendida", Toast.LENGTH_SHORT).show()
                }
        }

        btnApagar.setOnClickListener {
            database.child("estado_bomba").setValue(false)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bomba Apagada", Toast.LENGTH_SHORT).show()
                }

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
    }
}