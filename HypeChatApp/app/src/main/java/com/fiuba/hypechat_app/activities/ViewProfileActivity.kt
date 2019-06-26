package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.UserProfile
import com.fiuba.hypechat_app.models.Moi
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.CircleImageViewProfile
import kotlinx.android.synthetic.main.activity_profile.etNameProfile
import kotlinx.android.synthetic.main.activity_profile.etSurnameProfile
import kotlinx.android.synthetic.main.activity_profile.etUsernameProfile
import kotlinx.android.synthetic.main.activity_profile.rvOrganizationsProfile
import kotlinx.android.synthetic.main.activity_view_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getProfileDataFromSV()

        btnResumeProfile.setOnClickListener {
            val intent = Intent(this, ManagementActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getProfileDataFromSV() {
        RetrofitClient.instance.getUserProfile(Moi.getUserProfile())
            .enqueue(object : Callback<UserProfile> {
                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    if (response.isSuccessful) {
                        loadFields(response.body()!!)
                    } else {
                        Toast.makeText(baseContext, "Failed to load profile ", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun loadFields(body: UserProfile) {
        val adapter = GroupAdapter<ViewHolder>()
        etNameProfile2.setText(body.name)
        etSurnameProfile2.setText(body.surname)
        etUsernameProfile2.setText(body.username)
        Picasso.get().load(body.url).into(CircleImageViewProfile)

        body.organizations.forEach {
            adapter.add(ListItemWorkgroupAndChannel(it))
            rvOrganizationsProfile.adapter = adapter
        }

    }
}
