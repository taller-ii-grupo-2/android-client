package com.fiuba.hypechat_app.activities

import android.content.Context
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity

import com.fiuba.hypechat_app.*
import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Moi

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

        getRolsFromSv()



        btnDeleteWorkgroup.setOnClickListener {
            if (rol=="Member" || rol=="Admin") {
                btnDeleteWorkgroup.isEnabled = false
                Toast.makeText(baseContext, "You don't have permission to delete workgroup", Toast.LENGTH_SHORT).show()
            }
            else  deleteAndSendToSV()

        }


    }

    fun deleteAndSendToSV(){
        val delWorkgroup = deleteOrga(Moi.getCurrentOrganizationName())
        RetrofitClient.instance.deleteOrganization(delWorkgroup)
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

    private fun getRolsFromSv(){
        RetrofitClient.instance.getUsersTypes(Moi.getOrgaNameForOrgaFetch())
            .enqueue(object : Callback<List<Types>> {
                override fun onFailure(
                    call: Call<List<Types>>
                    , t: Throwable
                ) {
                    Toast.makeText(baseContext, "Error loading types data", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<Types>>
                    , response: Response<List<Types>>
                ) {
                    if (response.isSuccessful) {
                        val listTypes = response.body()!!
                        setFields(listTypes)

                    } else {
                        Toast.makeText(baseContext, "Failed loading types data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun setFields(listTypes: List<Types>) {
    /*    var listTypes = ArrayList<Types>()
        val type1 = Types ("turi.77@gmail.com", "Member")
        val type2 = Types ("damato@gmail.com", "Member")
        val type3 = Types ("jax@gmail.com", "Creator")
        val type4 = Types ("ture@gmail.com", "Admin")

        listTypes.add(type1)
        listTypes.add(type2)
        listTypes.add(type3)
        listTypes.add(type4)*/


        val me = FirebaseAuth.getInstance().currentUser!!.email

        listTypes.forEach {
            if (it.mail == me)
                rol = it.type

        }
        val adapter = GroupAdapter<ViewHolder>()


        listTypes.forEach {
            adapter.add(ItemUser(it,adapter, baseContext, rol))

        }
        rvUsers.adapter = adapter

        Moi.getChannelList().forEach {
            adapter.add(ItemChannel(it, adapter, rol))

        }
        rvChannels.adapter = adapter

        
    }
}


class ItemUser(var item: Types,var adapter: GroupAdapter<ViewHolder>,var  contexto: Context, val rol: String) : Item<ViewHolder>() {
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
                    "Delete User" -> {deleteAndSendToSV(item.mail)
                                        adapter.removeGroup(position)
                                        adapter.notifyDataSetChanged() }
                    "Make admin" -> {changeRol(item.mail, "Admin")
                                     viewHolder.itemView.txtType.text = "Admin"
                                    changeMenu(popMenu,"Admin")}
                    "Make member" -> {changeRol(item.mail,"Member")
                                    viewHolder.itemView.txtType.text = "Member"
                                    changeMenu(popMenu,"Member")}
                    "View profile user" ->  viewUserProfile(item.mail,contexto)
                }
                true


            }
            popMenu.show()

        }
    }

    private fun changeMenu(popMenu: PopupMenu, type: String) {
        if (type == "Admin"){
            popMenu.menu.getItem(1).isEnabled = true
            popMenu.menu.getItem(2).isEnabled = true
        }
        if (type == "Member"){
            popMenu.menu.getItem(1).isEnabled = true
            popMenu.menu.getItem(2).isEnabled = false
        }
    }


    override fun getLayout(): Int {
        return R.layout.delete_row_users
    }

    fun viewUserProfile(mail:String, context:Context){
        Moi.updateUserProfile(mail)
        val intent = Intent(context, ViewProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(context,intent,null)


    }

    fun deleteAndSendToSV(mail:String){
        val delUser = deleteUser(Moi.getCurrentOrganizationName(), mail)
        RetrofitClient.instance.deleteUser(delUser)
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
    fun changeRol(mail:String, rol: String){
        val rola = Types(mail,rol)
        RetrofitClient.instance.updateRol(Moi.getCurrentOrganizationName(),rola)
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



class ItemChannel(var item: Channel, var adapter: GroupAdapter<ViewHolder>, val rol: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtChannelDelete.text = item.channel_name

        if (rol == "Member") {
            viewHolder.itemView.btnDeleteChannel.isEnabled = false
            viewHolder.itemView.btnDeleteChannel.background = null
        }

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
