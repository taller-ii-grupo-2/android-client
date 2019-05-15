package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.SignInActivity
import com.fiuba.hypechat_app.models.Moi
import com.fiuba.hypechat_app.models.SocketHandler
import com.fiuba.hypechat_app.models.Workgroup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_workspace.*
import kotlinx.android.synthetic.main.workgroup_row.view.*


class WorkspaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        val SERVER_URL = "https://hypechatgrupo2-app-server-stag.herokuapp.com/"
        SocketHandler.setSocket(SERVER_URL, Moi.get_mail())
        verifyUserIsSignIn()

      /*  val adapter = GroupAdapter<ViewHolder>()
        adapter.add(WorkgroupItem())
        adapter.add(WorkgroupItem())
        adapter.add(WorkgroupItem())

        rvWorkgroup.adapter = adapter
*/

        fetchWorkgroups()


        }

    companion object {
        val GROUP_KEY = "GROUP_KEY"
    }

    private fun fetchWorkgroups() {
        val ref = FirebaseDatabase.getInstance().getReference("/workgroup")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

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
                    intent.putExtra(GROUP_KEY, workgroupItem.currentWorkgroup)
                    startActivity(intent)

                }
                rvWorkgroup.adapter = adapter
            }



            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsSignIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
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
        when(item?.itemId){
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




class WorkgroupItem( val currentWorkgroup: Workgroup): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtViewWorkgroup.text = currentWorkgroup.name

        Picasso.get().load(currentWorkgroup.urlImage).into(viewHolder.itemView.imgViewLogo)
    }

    override fun getLayout(): Int {
        return R.layout.workgroup_row
    }
}
