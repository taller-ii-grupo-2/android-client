package com.fiuba.hypechat_app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.FacebookSdk.getApplicationContext
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.WorkgroupPhotoAndName
import com.fiuba.hypechat_app.activities.user_registration.SignInActivity
import com.fiuba.hypechat_app.models.Moi
import com.fiuba.hypechat_app.models.SocketHandler

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_workspace.*
import kotlinx.android.synthetic.main.workgroup_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WorkspacesListActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        verifyUserIsSignedIn()
        SocketHandler.setSocket(Moi.SERVER_URL, Moi.getMail())

        fetchWorkgroupsPhotoAndName()
    }

    private fun fetchWorkgroupsPhotoAndName() {
        val adapter = GroupAdapter<ViewHolder>()
        RetrofitClient.instance.getWorkgroupNameAndPhotoProfile()
            .enqueue(object : Callback<List<WorkgroupPhotoAndName>> {
                override fun onFailure(call: Call<List<WorkgroupPhotoAndName>>, t: Throwable) {
                    Toast.makeText(baseContext, "Error loading workgroup data", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<WorkgroupPhotoAndName>>,
                    response: Response<List<WorkgroupPhotoAndName>>
                ) {
                    if (response.isSuccessful) {
                        //Toast.makeText(baseContext, response.message(), Toast.LENGTH_SHORT).show()

                        val workgroupList = response.body()
                        workgroupList?.forEach {
                            adapter.add(WorkgroupItem(it))
                            rvWorkgroup.adapter = adapter
                        }

                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        adapter.setOnItemClickListener { item, view ->

            val workgroupItem = item as WorkgroupItem
            Moi.setOrgaNameForOrgaFetch(workgroupItem.getWorkgroupName())
            Moi.updateCurrentChannelName("General")

            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verifyUserIsSignedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.workspace_signout -> {
                FirebaseAuth.getInstance().signOut()
                val galletita = RetrofitClient.CookiesInterceptor()
                galletita.clearCookie()
                SocketHandler.disconnect()
                resetLoginInfoInSharedPref()
                unsubscribeFromNotifTopic(Moi.getMail().replace("@","~at~"))
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.workspace_createNew -> {
                val intent = Intent(this, WorkspaceCreationActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun unsubscribeFromNotifTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "successfully unsubscribed from notif topic"
                if (!task.isSuccessful) {
                    msg = "couldn't get unsubscribed from notif topic"
                }
                Log.d(null, msg)
            }
    }
}

private fun resetLoginInfoInSharedPref() {
    val sharedPreferences = getApplicationContext().getSharedPreferences("loginPreferences", Context.MODE_PRIVATE)

    val editor = sharedPreferences.edit()
    editor.putString("mail", "")
    editor.putString("password", "")
    editor.putString("token", "")
    editor.putString("cookie", "")
    editor.apply()
}

class WorkgroupItem(val currentWorkgroup: WorkgroupPhotoAndName) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtViewWorkgroup.text = currentWorkgroup.name

        Picasso.get().load(currentWorkgroup.urlImage).into(viewHolder.itemView.imgViewLogo)
    }

    override fun getLayout(): Int {
        return R.layout.workgroup_row
    }

    fun getWorkgroupName(): String {
        return currentWorkgroup.name
    }
}
