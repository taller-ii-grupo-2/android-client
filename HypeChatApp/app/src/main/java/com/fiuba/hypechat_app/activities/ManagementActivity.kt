package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.deleteChannel
import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Moi
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.delete_row_channels.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setFields()

    }

    private fun setFields() {
        val adapter = GroupAdapter<ViewHolder>()
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
