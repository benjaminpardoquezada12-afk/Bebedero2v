package com.example.bebedero2v

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistorialActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance(
        "https://bebederoiot-default-rtdb.firebaseio.com/"
    ).reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val lvHistorial = findViewById<ListView>(R.id.lvHistorial)
        val btnVolverDashboard = findViewById<Button>(R.id.btnVolverDashboard)

        btnVolverDashboard.setOnClickListener {
            finish()
        }

        // Lista en memoria donde guardaremos los textos
        val listaRegistros = ArrayList<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaRegistros)
        lvHistorial.adapter = adapter

        // Leemos todos los registros de la base de datos
        database.child("historial_consumo").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRegistros.clear()

                for (child in snapshot.children) {
                    val fecha = child.child("fecha").getValue(String::class.java) ?: "Sin fecha"
                    val litros = child.child("litros").getValue(Double::class.java) ?: 0.0

                    // Añade cada fila con el formato "Fecha: Litros"
                    listaRegistros.add("Consumo ($fecha): $litros Litros")
                }

                // Notifica a la lista que hay nuevos elementos para dibujarlos
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de error si se corta la conexión
            }
        })
    }
}