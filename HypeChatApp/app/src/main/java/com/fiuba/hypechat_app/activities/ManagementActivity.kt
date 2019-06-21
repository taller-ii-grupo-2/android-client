package com.fiuba.hypechat_app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.hypechat_app.*
import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Moi
import com.google.firebase.FirebaseAppLifecycleListener
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.delete_row_channels.view.*
import kotlinx.android.synthetic.main.delete_row_users.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagementActivity : AppCompatActivity() {
    var rol = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setFields()

    }

    private fun setFields() {
        var listTypes = ArrayList<Types>()
        val type1 = Types ("turi.77@gmail.com", "Member")
        val type2 = Types ("damato@gmail.com", "Member")
        val type3 = Types ("jax@gmail.com", "Creator")
        val type4 = Types ("ture@gmail.com", "Admin")

        listTypes.add(type1)
        listTypes.add(type2)
        listTypes.add(type3)
        listTypes.add(type4)


        val me = FirebaseAuth.getInstance().currentUser!!.email

        listTypes.forEach {
            if (it.mail == me)
                rol = it.type

        }
        val adapter = GroupAdapter<ViewHolder>()
        val adapter2 = GroupAdapter<ViewHolder>()

        listTypes.forEach {
            adapter.add(ItemUser(it,adapter2, baseContext, rol))

        }
        rvUsers.adapter = adapter2

        Moi.getChannelList().forEach {
            adapter.add(ItemChannel(it, adapter))

        }
        rvChannels.adapter = adapter

        /*adapter.setOnItemClickListener { item, view ->

            val workgroupItem = item as WorkgroupItem
            Moi.setOrgaNameForOrgaFetch(workgroupItem.getWorkgroupName())
            Moi.updateCurrentChannelName("General")


        }*/
    }
}


class ItemUser(var item: Types, var adapter: GroupAdapter<ViewHolder>,val  contexto: Context, val rol: String) : Item<ViewHolder>() {
    //val yo = "Creator"

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtNameDelete.text = item.mail
        viewHolder.itemView.txtType.text = item.type

        viewHolder.itemView.btnMenuUser.setOnClickListener {

            val popMenu = PopupMenu(contexto, it)

            when (rol){
                "Member" -> popMenu.inflate(R.menu.user_miembro)
                "Admin" -> popMenu.inflate(R.menu.user_admin)
                "Creator" -> popMenu.inflate(R.menu.user_menu)
            }

            validateOptions(rol, item, popMenu)

            popMenu.setOnMenuItemClickListener {

                when (it.title) {
                    "Delete User" -> Toast.makeText(contexto, "Delete user", Toast.LENGTH_SHORT).show()
                    "Make admin" -> Toast.makeText(contexto, "Make admin", Toast.LENGTH_SHORT).show()
                    "Make member" -> Toast.makeText(contexto, "Make member", Toast.LENGTH_SHORT).show()
                    "View profile user" ->  Toast.makeText(contexto,  "View profile user" , Toast.LENGTH_SHORT).show()
                }
                true


            }
            popMenu.show()

        }
    }
    override fun getLayout(): Int {
        return R.layout.delete_row_users
    }

    fun validateOptions(yo:String,item: Types,popMenu: PopupMenu){
        if ((yo == "Admin" && item.type == "Creator") || (yo == "Admin" && item.type == "Admin")  ){
            popMenu.menu.getItem(0).isEnabled = false
        }

        if (yo == "Creator" && item.type == "Admin"){
            popMenu.menu.getItem(1).isEnabled = false
        }

        if (yo == "Creator" && item.type == "Creator"){
            popMenu.menu.getItem(0).isEnabled = false
            popMenu.menu.getItem(1).isEnabled = false
            popMenu.menu.getItem(2).isEnabled = false
            popMenu.menu.getItem(3).isEnabled = false
        }

        if (yo == "Creator" && item.type == "Member"){
            popMenu.menu.getItem(2).isEnabled = false
        }

    }
}



class ItemChannel(var item: Channel, var adapter: GroupAdapter<ViewHolder>) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtChannelDelete.text = item.channel_name

        viewHolder.itemView.btnDeleteChannel.setOnClickListener {
            adapter.removeGroup(position)
            adapter.notifyDataSetChanged()
            sendMessageToSv(item.channel_name)

        }

    }
    private fun sendMessageToSv(channel_name: String) {
        val orga = Moi.getCurrentOrganizationName()
        val deleteChannel = deleteChannel(orga, channel_name)
        RetrofitClient.instance.deleteChannel(deleteChannel)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {

                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {


                    } else {

                    }
                }
            })

    }

    override fun getLayout(): Int {
        return R.layout.delete_row_channels
    }
}
