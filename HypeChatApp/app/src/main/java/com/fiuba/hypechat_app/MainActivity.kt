package com.fiuba.hypechat_app

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDatabase = findViewById(R.id.btnDatabase) as Button
        btnDatabase.setOnClickListener{
            val txt = findViewById(R.id.txtCheckpoint) as TextView
            txt.setText("Devolver base de datos")
        }
    }
}
