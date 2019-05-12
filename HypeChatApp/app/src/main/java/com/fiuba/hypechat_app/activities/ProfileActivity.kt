package com.fiuba.hypechat_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fiuba.hypechat_app.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
/*
* Armar interfaz basica de profile
Cargar imagen por defecto si no suben una
Cargar localizacion gps
Añadir al profile el servicio de cambio de pw

*/

}
