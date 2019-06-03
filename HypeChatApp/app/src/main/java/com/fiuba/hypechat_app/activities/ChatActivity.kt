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
import com.fiuba.hypechat_app.*

import com.fiuba.hypechat_app.models.Moi
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
import kotlinx.android.synthetic.main.app_bar_nav_drawer.*
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.nav_header_nav_drawer.view.*


class ChatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

       //MoiHardcoding(navView)

      //  val workspace= fetchWorgroupData()

        //val workGroup = intent.getParcelableExtra<Workgroup>(WorkspacesListActivity.GROUP_KEY)
        //toolbar.title = Moi.get_current_organization()


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        //setDataIntoNavBar(navView, workspace)

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

    private fun MoiHardcoding(navView: NavigationView) {

        var listChannel: MutableList<String> = ArrayList()
        var members: MutableList<String> = ArrayList()
        listChannel.add("Channel1")
        listChannel.add("general")
        listChannel.add("Channel Futbol")
        members.add("Matias")
        members.add("Agustin")
        members.add("Augusto")

        var workspace = Workspace(
            "Este es una orga hardcodeada de prueba", "Bienvenido a esta orga nueva",
            "https://firebasestorage.googleapis.com/v0/b/hypechatapp-ebdd6.appspot.com/o/images%2Ffiuba_logo.png?alt=media&token=ed9d116e-1b68-423b-87a3-06b6af21fb37",
            listChannel, members
        )

        //Guardo el objeto que recibo en Moi
        Moi.save_workspace(workspace)

        //Seteo el texto de la toolbar con el nombre de la orga
        toolbar.title = Moi.get_current_organization_name()
        val headerView = navView.getHeaderView(0)

        //Seteo logo de la orga
        Picasso.get().load(Moi.get_current_organization().urlImage).into(headerView.imgNavLogo)

        //Seteo campos del NavView
        headerView.txtNameOrg.text = Moi.get_current_organization_name()
        headerView.txtDescOrg.text = Moi.get_current_organization().description
        Moi.set_current_channel("general")

        //Menu del NavView
        val navMenu = navView.menu
        navMenu.add (0,0,0,"Add channel").setIcon(R.mipmap.newchannel) // Esto va fijo, de aca se dispara el activity para agregar un channel

        val channels = navMenu.addSubMenu("Channels")

        //Obtengo lista de canales por medio de Moi
        val listChannels = Moi.get_channel_list()

        //Agrego lista de strings con miembros a Moi para despues mostrar en NavView
        Moi.get_current_organization().setListMembers(workspace.members)
        var contador = 0
        listChannels.forEach {
            //channels.add(it.channel_name)
            channels.add(0, contador, 1, it.channel_name)
            contador++
        }

        //Agrego miembros de la orga al NavView
        val directMessages = navMenu.addSubMenu("Direct Messages")
        val listMembers = Moi.get_current_organization().getListMembers()
        var contadorMiembros = 0
        listMembers.forEach {
            directMessages.add(0, contador,1,it)
            contador++
        }





    }


    private fun fetchWorgroupData(): Workspace {
        var workspace: Workspace? = null
        val adapter = GroupAdapter<ViewHolder>()

        Log.d("MOI PRINT ->>>", Moi.get_current_organization_name())
        RetrofitClient.instance.getWholeOrgaData(Moi.get_current_organization_name())
            .enqueue(object : Callback<Workspace> {
                override fun onFailure(call: Call<Workspace>, t: Throwable) {
                    Toast.makeText(baseContext, "Error loading workgroup data", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<Workspace>,
                    response: Response<Workspace>
                ) {
                    if (response.isSuccessful) {

                        workspace = response.body()!!
                        Moi.save_workspace(workspace!!)


                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        return workspace!!
    }


    private fun setDataIntoNavBar(navView: NavigationView, workspace: Workspace) {
        val headerView = navView.getHeaderView(0)
        Picasso.get().load(workspace.urlImage).into(headerView.imgNavLogo)
        headerView.txtNameOrg.text = Moi.get_current_organization_name()
        headerView.txtDescOrg.text = workspace.description

        var navMenu = navView.menu
        navMenu.add("Add channel")
        var channels = navMenu.addSubMenu("Channels")
        channels.add("Futbol")
        channels.add("Tenis")
        var directMessages = navMenu.addSubMenu("Direct Messages")
        directMessages.add("yo")
    }

    @Synchronized
    private fun receiveMessages(adapter: GroupAdapter<ViewHolder>) {
        //var socket = SocketHandler.getSocket()
        SocketHandler.getSocket().on("message") { args ->
            val getData = args.joinToString()
            Log.d("SocketHandler", getData)
            runOnUiThread {
                adapter.add(ChatItem(getData))
            }
        }
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
        /*
         * Inflate the menu; this adds items to the action bar if it is present.
         */
        menuInflater.inflate(R.menu.nav_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        return when (item.itemId) {
            R.id.action_profile -> setProfileActivity()
            R.id.action_maps -> showMapActivity()

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMapActivity(): Boolean {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, 20)
        return true
    }

    private fun setProfileActivity(): Boolean {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivityForResult(intent, 20)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val listChannels = Moi.get_channel_list()
        val listMembers = Moi.get_current_organization().getListMembers()
        var contador = 0

        // Handle navigation view item clicks here.
        if(item.order==0) {
            Toast.makeText(this, "Add channel", Toast.LENGTH_SHORT).show()

            return true
        }


        listChannels.forEach {
            when (item.itemId ) {
                contador -> Toast.makeText(this, it.channel_name, Toast.LENGTH_SHORT).show()

            }
            contador++
        }
        listMembers.forEach {
            when (item.itemId) {
                contador -> Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

            }
            contador++

        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }


}

class ChatItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtChatRow.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}


