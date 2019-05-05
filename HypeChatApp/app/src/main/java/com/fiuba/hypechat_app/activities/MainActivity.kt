package com.fiuba.hypechat_app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Button
import android.widget.TextView
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnDatabase = findViewById(R.id.btnDatabase) as Button
        btnDatabase.setOnClickListener{
            fetchJSON()
            changeViewToLogin()
        }
    }

    private fun changeViewToLogin() {
        startActivity(Intent(this,SignInActivity::class.java))
    }

    fun fetchJSON() {
         println("Attempt to fetch JSON")

         val url = "https://hypechatgrupo2-app-server.herokuapp.com/android"

         val request = Request.Builder()
             .url(url)
             .build()

         val client = OkHttpClient()
         client.newCall(request).enqueue(object: Callback{
             override fun onResponse(call: Call, response: Response) {
                 val body = response.body()?.string()
                 println(body)
                 runOnUiThread {
                     showScreen(body.toString())
                 }

             }
             override fun onFailure(call: Call, e: IOException) {
                 println ("Fail to execute request")
             }
         })


    }

    fun showScreen(textInput:String){
        val txt = findViewById(R.id.txtCheckpoint) as TextView
        txt.setText(textInput)

    }



}
