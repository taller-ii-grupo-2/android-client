package com.fiuba.hypechat_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Moi
import kotlinx.android.synthetic.main.activity_channel_creation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChannelCreationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_creation)




        btnChannelCreate.setOnClickListener {
            if (validateFields())
                sendDataToSv()
        }


    }

    private fun sendDataToSv() {
        val name = et_channelName.text.toString()
        val desc = et_channelDesc.text.toString()
        val public = sw_public.isChecked
        val orga = Moi.getCurrentOrganizationName()
        val channel = Channel(orga,name,public,desc)


        RetrofitClient.instance.createChannel(channel)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully channel created", Toast.LENGTH_SHORT).show()
                        Moi.addChannel(channel)
                        //A donde habria que ir ?
                    } else {
                        Toast.makeText(baseContext, "Failed to create channel", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun validateFields() : Boolean {
        if (et_channelName.text.toString().isEmpty()) {
            et_channelName.error = "Plase enter channel name"
            et_channelName.requestFocus()
            return false
        }  else {
            return true
        }

    }
}
