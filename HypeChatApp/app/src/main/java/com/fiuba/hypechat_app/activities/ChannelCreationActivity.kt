package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Moi
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_channel_creation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChannelCreationActivity : AppCompatActivity() {

    private  var chip_groupp : ChipGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_creation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnChannelCreate.setOnClickListener {
            if (validateFields())
                sendDataToSv()
        }

        sw_public.setOnClickListener {
            if (sw_public.isChecked == false){
                et_username.isEnabled = true
                btnAdd.isEnabled = true
            }

            if (sw_public.isChecked == true){
                et_username.isEnabled = false
                btnAdd.isEnabled = false
                chip_groupp!!.removeAllViews()
            }
        }



        btnAdd.setOnClickListener {
            if (haveContent()) {
                val email = et_username.text.toString()
                et_username.setText(null)
                val  inflater = LayoutInflater.from (this@ChannelCreationActivity)
                val chip_item = inflater.inflate(R.layout.chip_item, null, false) as Chip
                chip_item.text = email
                chip_item.setOnCloseIconClickListener {
                    chip_group!!.removeView(it)
                }
                chip_group!!.addView(chip_item)
                chip_groupp = chip_group
            }
            //Toast.makeText(baseContext, getEmailsFromChipGroup(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun haveContent(): Boolean {
        if (et_username.text.toString().isEmpty()) {
            et_username.requestFocus()
            return false
        }
        return true

    }

    private fun sendDataToSv() {
        val name = et_channelName.text.toString()
        val desc = et_channelDesc.text.toString()
        val public = sw_public.isChecked
        val orga = Moi.getCurrentOrganizationName()
        val invitations = getEmailsFromChipGroup()

        val channel = Channel(orga, name, public, desc, invitations)


        RetrofitClient.instance.createChannel(channel)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully channel created", Toast.LENGTH_SHORT).show()
                        Moi.addChannel(channel)
                        val intent = Intent(baseContext, ChatActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Failed to create channel", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun getEmailsFromChipGroup(): List<String> {
        var result : StringBuilder = java.lang.StringBuilder("")
        for (i in 0 until chip_groupp!!.childCount){
            val chip = chip_groupp!!.getChildAt(i) as Chip
            result.append(chip.text).append(",")


        }

        var list = result.dropLast(1).trim().split(",")
            .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
            .toList()
        return list

    }

    private fun validateFields(): Boolean {
        if (et_channelName.text.toString().isEmpty()) {
            et_channelName.error = "Plase enter channel name"
            et_channelName.requestFocus()
            return false
        } else {
            return true
        }
    }
}
