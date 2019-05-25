package com.fiuba.hypechat_app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.WorkgroupPhotoAndName
import com.fiuba.hypechat_app.activities.user_registration.SignInActivity
import com.fiuba.hypechat_app.models.Moi
import com.fiuba.hypechat_app.models.SocketHandler

import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.workgroup_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WorkspacesListActivity : AppCompatActivity() {

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        val SERVER_URL = "https://hypechatgrupo2-app-server-stag.herokuapp.com/"

        verifyUserIsSignedIn()
        SocketHandler.setSocket(SERVER_URL, Moi.get_mail())

        fetchWorkgroupsPhotoAndName()
    }

    private fun fetchWorkgroupsPhotoAndName() {
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
                        val adapter = GroupAdapter<ViewHolder>()
                        val workgroupList = response.body()
                        workgroupList?.forEach {
                            if (it != null)
                                adapter.add(WorkgroupItem(it))
                        }

                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    companion object {
        val GROUP_KEY = "GROUP_KEY"
    }

    // FUNCTION TO FETCH DATA WITH FIREBASE
    /* private fun fetchWorkgroups() {
         val ref = FirebaseDatabase.getInstance().getReference("/workgroup")
         ref.addListenerForSingleValueEvent(object : ValueEventListener {

             override fun onDataChange(p0: DataSnapshot) {
                 val adapter = GroupAdapter<ViewHolder>()

                 p0.children.forEach {
                     Log.d("New workgroups", it.toString())
                     val workgroup = it.getValue(Workgroup::class.java)
                     if (workgroup != null) {
                         adapter.add(WorkgroupItem(workgroup))
                     }
                 }

                 //rvWorkgroup.adapter = adapter

                 adapter.setOnItemClickListener { item, view ->

                     val workgroupItem = item as WorkgroupItem
                     val intent = Intent(view.context, NavDrawerActivity::class.java)
                     Moi.update_current_organization(workgroupItem.currentWorkgroup.name)
 //                    TODO cambiar esto de channel.
                     Moi.update_current_channel("general")
                     intent.putExtra(GROUP_KEY, workgroupItem.currentWorkgroup)
                     startActivity(intent)

                 }
                 rvWorkgroup.adapter = adapter
             }

             override fun onCancelled(p0: DatabaseError) {

             }
         })
     }*/

    private fun verifyUserIsSignedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Moi.set_id_values(FirebaseAuth.getInstance().currentUser!!.email.toString())
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
}


class WorkgroupItem(val currentWorkgroup: WorkgroupPhotoAndName) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtViewWorkgroup.text = currentWorkgroup.name

        Picasso.get().load(currentWorkgroup.urlImage).into(viewHolder.itemView.imgViewLogo)
    }

    override fun getLayout(): Int {
        return R.layout.workgroup_row
    }
}
