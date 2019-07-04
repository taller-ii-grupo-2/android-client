package com.fiuba.hypechat_app.activities.user_registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fiuba.hypechat_app.*
import com.fiuba.hypechat_app.activities.ChatActivity
import com.fiuba.hypechat_app.models.Moi
import kotlinx.android.synthetic.main.activity_add_member_to_channel.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemberToChannel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member_to_channel)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnAddMemberToChannel.setOnClickListener {
            if (validateFields())
                sendNewMemberToSv()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(baseContext, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun sendNewMemberToSv() {
        val orga = Moi.getOrgaNameForOrgaFetch()
        val channel = Moi.getCurrentChannelName()
        val mail = et_newMemberChannel.text.toString()
        val member = newChannelMember(orga, channel, mail)
        RetrofitClient.instance.addMemberToChannel(member)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully member added", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(baseContext, "Failed to add member", Toast.LENGTH_SHORT).show()
                    }
                }
            })

    }

    private fun validateFields() : Boolean {
        if (et_newMemberChannel.text.toString().isEmpty()) {
            et_newMemberChannel.error = "Plase enter email"
            et_newMemberChannel.requestFocus()
            return false

        }
        return true
    }
}
