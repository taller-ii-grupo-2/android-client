package com.fiuba.hypechat_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_organization_selection.*

class OrganizationSelectionActivity : AppCompatActivity() {
    private val SERVER_URL = "http://192.168.100.105:5000/" //TODO put correct address for production.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_selection)
        SocketHandler.setSocket(SERVER_URL)

        val btnToChat = findViewById(R.id.btnGotoChat) as Button
        btnGotoChat.setOnClickListener{
            changeViewToChat()
        }
    }

    private fun changeViewToChat() {
        startActivity(Intent(this,ChatActivity::class.java))
    }
}
