package com.fiuba.hypechat_app.activities

import android.content.Context
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
import com.fiuba.hypechat_app.models.Channel

import com.fiuba.hypechat_app.models.Moi
import com.fiuba.hypechat_app.models.SocketHandler
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
import kotlinx.android.synthetic.main.chat_row_receive.view.*
import kotlinx.android.synthetic.main.nav_header_nav_drawer.view.*
import org.json.JSONObject
import ru.noties.markwon.Markwon
import ru.noties.markwon.image.ImagesPlugin


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

        fetchWorgroupData(navView)


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        //setDataIntoNavBar(navView, workspace)

        val adapter = GroupAdapter<ViewHolder>()
        rvChat.adapter = adapter
        receiveMessages(adapter)
        send_button_chat_log.setOnClickListener {
            val textToSend = txtChat.text.toString()
            SocketHandler.send(textToSend)
            txtChat.text = null
        }
    }

    private fun setView(navView: NavigationView) {
        //Seteo el texto de la toolbar con el nombre de la orga
        toolbar.title = Moi.getCurrentOrganizationName()
        toolbar.subtitle = Moi.getCurrentChannelName()
        val headerView = navView.getHeaderView(0)

        //Seteo logo de la orga
        Picasso.get().load(Moi.getUrlImageForCurrentOrga()).into(headerView.imgNavLogo)

        //Seteo campos del NavView
        headerView.txtNameOrg.text = Moi.getCurrentOrganizationName()
        headerView.txtDescOrg.text = Moi.getCurrentOrganizationsDescription()

        //Menu del NavView
        val navMenu = navView.menu
        navMenu.add(0, 0, 0, "Add channel")
            .setIcon(R.mipmap.newchannel) // Esto va fijo, de aca se dispara el activity para agregar un channel

        val channels = navMenu.addSubMenu("Channels")

        //Obtengo lista de canales por medio de Moi
        val listChannels = Moi.getChannelList()

        var contador = 0
        listChannels.forEach {
            //channels.add(it.channel_name)
            channels.add(0, contador, 1, it.channel_name)
            contador++
        }

        //Agrego miembros de la orga al NavView
        val directMessages = navMenu.addSubMenu("Direct Messages")
        val listMembers = Moi.getCurrentOrganization().getListMembers()
        var contadorMiembros = 0
        listMembers.forEach {
            directMessages.add(0, contador, 1, it)
            contador++
        }
    }


    private fun fetchWorgroupData(navView: NavigationView) {
        //var workspace: Workspace? = null
        val adapter = GroupAdapter<ViewHolder>()

        Log.d("MOI PRINT ->>>", Moi.getOrgaNameForOrgaFetch())
        RetrofitClient.instance.getWholeOrgaData(Moi.getOrgaNameForOrgaFetch())
            .enqueue(object : Callback<Workspace> {
                override fun onFailure(call: Call<Workspace>, t: Throwable) {
                    Toast.makeText(baseContext, "Error loading workgroup data", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Workspace>, response: Response<Workspace>) {
                    if (response.isSuccessful) {
                        val workspace = response.body()!!
                        Moi.saveWorkspace(workspace!!)
                        Log.d("MOI PRINT ->>>", "ENTRE AL ON RESPONSEEEEEEEEE")
                        setView(navView)
                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }


    private fun setDataIntoNavBar(navView: NavigationView, workspace: Workspace) {
        val headerView = navView.getHeaderView(0)
        Picasso.get().load(workspace.urlImage).into(headerView.imgNavLogo)
        headerView.txtNameOrg.text = Moi.getCurrentOrganizationName()
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
            val jsonData = JSONObject(getData)

            val orga = jsonData.getString("organization")
            val channel = jsonData.getString("channel")
            val dm_dest = jsonData.getString("dm_dest")
            val author_mail = jsonData.getString("author_mail")
            val timestamp = jsonData.getString("timestamp")
            val msg_body = jsonData.getString("msg_body")

            Log.d("SocketHandler", getData)
            runOnUiThread {
                if (author_mail == Moi.getMail()){
                    adapter.add(ChatItem(baseContext, msg_body))
                }else{
                    adapter.add(ChatItemReceive(baseContext, getData))
                }
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
            R.id.add_member_to_workgroup -> showAddMemberActivity()

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddMemberActivity(): Boolean {
        val intent = Intent(this, AddMemberToWorkgroupActivity::class.java)
        startActivityForResult(intent, 20)
        return true
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
        val listChannels = Moi.getChannelList()
        val listMembers = Moi.getCurrentOrganization().getListMembers()
        var contador = 0

        // Handle navigation view item clicks here.
        if (item.order == 0) {
            Toast.makeText(this, "Add channel", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChannelCreationActivity::class.java)
            startActivityForResult(intent, 20)
            return true
        }


        listChannels.forEach {
            when (item.itemId) {
                contador -> {
                    Toast.makeText(this, it.channel_name, Toast.LENGTH_SHORT).show()

                    Moi.updateCurrentChannelName(it.channel_name)

                    Moi.updateCurrentDmDest("")
                    Moi.updateCurrentDmDestName("")

                    toolbar.subtitle =  Moi.getCurrentChannelName()
                }
            }
            contador++
        }
        listMembers.forEach {
            when (item.itemId) {
                contador -> {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    Moi.updateCurrentDmDestName(it)

                    Moi.updateCurrentChannelName("")
                    Moi.updateCurrentChannel(Channel(""))

                    toolbar.subtitle = Moi.getCurrentDmDestName()

                }
            }
            contador++
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

class ChatItem(baseContext: Context, val text: String) : Item<ViewHolder>() {
    // obtain an instance of Markwon
    var markwon = Markwon.builder(baseContext).usePlugin(ImagesPlugin.create(baseContext)).build()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // parse markdown to commonmark-java Node
        val node = markwon.parse(text)

        // create styled text from parsed Node
        val markdown = markwon.render(node)

        // use it on a TextView
        this.markwon.setParsedMarkdown(viewHolder.itemView.txtChatRow, markdown)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

class ChatItemReceive(baseContext: Context, val text: String) : Item<ViewHolder>() {
    // obtain an instance of Markwon
    var markwon = Markwon.builder(baseContext).usePlugin(ImagesPlugin.create(baseContext)).build()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // parse markdown to commonmark-java Node
        val node = markwon.parse(text)

        // create styled text from parsed Node
        val markdown = markwon.render(node)

        // use it on a TextView
        this.markwon.setParsedMarkdown(viewHolder.itemView.txtChatRowReceive, markdown)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_receive
    }
}

