package com.example.bebedero2v

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val campoEmail = findViewById<EditText>(R.id.etEmail)
        val campoClave = findViewById<EditText>(R.id.etPassword)
        val botonIngresar = findViewById<Button>(R.id.btnLogin)

        botonIngresar.setOnClickListener {
            val email = campoEmail.text.toString().trim()
            val clave = campoClave.text.toString().trim()

            if (email == "admin@inacap.cl" && clave == "123456") {
                Toast.makeText( this,  "Bienvenido Usuario", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Contraseña Incorrecta",  Toast.LENGTH_SHORT).show()
            }
        }
    }
}