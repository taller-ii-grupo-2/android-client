package com.fiuba.hypechat_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.models.Moi
import com.fiuba.hypechat_app.newMember
import kotlinx.android.synthetic.main.activity_add_member_to_workgroup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemberToWorkgroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member_to_workgroup)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        addMemberToWorkgroup.setOnClickListener {
           if (validateFields())
               sendNewMemberToSv()
        }
    }

    private fun sendNewMemberToSv() {
            val orga = Moi.getOrgaNameForOrgaFetch()
            val mail = et_newMemberWorkgroup.text.toString()
            val member = newMember(orga, mail)
            RetrofitClient.instance.addMemberToWorkgroup(member)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(baseContext, "Successfully member added", Toast.LENGTH_SHORT).show()
                            //A donde habria que ir ?
                        } else {
                            Toast.makeText(baseContext, "Failed to add member", Toast.LENGTH_SHORT).show()
                        }
                    }
                })

    }

    private fun validateFields() : Boolean {
        if (et_newMemberWorkgroup.text.toString().isEmpty()) {
            et_newMemberWorkgroup.error = "Plase enter channel name"
            et_newMemberWorkgroup.requestFocus()
            return false
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(et_newMemberWorkgroup.text.toString()).matches()) {
                et_newMemberWorkgroup.error = "Plase enter a valid email"
                et_newMemberWorkgroup.requestFocus()
                return false
            } else {
                return true
            }

        }
    }
}
