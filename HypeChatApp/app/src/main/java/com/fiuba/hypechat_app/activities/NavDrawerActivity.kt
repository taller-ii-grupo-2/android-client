package com.fiuba.hypechat_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast

import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.User
import com.fiuba.hypechat_app.models.SocketHandler
import com.fiuba.hypechat_app.models.Workgroup
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.content_nav_drawer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.nav_header_nav_drawer.view.*


class NavDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val workGroup =  intent.getParcelableExtra<Workgroup>(WorkspaceActivity.GROUP_KEY)
        toolbar.title = workGroup.name

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        setDataIntoNavBar(navView, workGroup)


        //fetchUsers()
        val adapter = GroupAdapter<ViewHolder>()
        rvChat.adapter = adapter
        receiveMessages(adapter)
        send_button_chat_log.setOnClickListener {
            val textToSend = txtChat.text.toString()
            adapter.add(ChatItem(textToSend))
            SocketHandler.send(textToSend)
            txtChat.text = null

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val workGroup =  data!!.getParcelableExtra<Workgroup>(WorkspaceActivity.GROUP_KEY)
    }

    private fun setDataIntoNavBar(navView: NavigationView, workGroup:Workgroup) {
        val headerView = navView.getHeaderView(0)
        Picasso.get().load(workGroup.urlImage).into(headerView.imgNavLogo)
        headerView.txtNameOrg.text =  workGroup.name
        headerView.txtDescOrg.text = workGroup.description

        var navMenu = navView.menu
        navMenu.add("Add channel")
        var channels = navMenu.addSubMenu("Channels")
        channels.add("Futbol")
        channels.add("Tenis")
        var directMessages = navMenu.addSubMenu("Direct Messages")
        directMessages.add("yo")
    }


    @Synchronized
    private fun receiveMessages(adapter :GroupAdapter<ViewHolder>) {
        //var socket = SocketHandler.getSocket()
        SocketHandler.getSocket().on("message"){
                args ->
            val getData = args.joinToString()
            Log.d("SocketHandler", getData)
            runOnUiThread {
                adapter.add(ChatItem(getData))
            }


        }
    }


    private fun fetchUsers() {
        RetrofitClient.instance.getListUsers()
            .enqueue(object: Callback<List<User>> {
                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, response.message(), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_profile -> setProfileActivity()
            R.id.action_maps-> showMapActivity()

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMapActivity(): Boolean {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent,20)
        return true
    }

    private fun setProfileActivity(): Boolean {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivityForResult(intent,20)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}


class ChatItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtChatRow.text = text

    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

