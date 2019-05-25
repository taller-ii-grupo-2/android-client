package com.fiuba.hypechat_app.models

import org.json.JSONObject
import java.io.File

object Moi {
    /* Moi name refers to Me - myself in french. This object is intended to contain all
     * info needed to identify oneself and to make oneself useful.
     * Oneself here denotes the logged user.
     * */

    /* personal info */
//    private lateinit var username: String
    private lateinit var mail: String

    private const val moi_filename = "moi"
    private lateinit var moi_fd: File

    private var dm_messages = mutableListOf<DirectMessage>()
    private var channel_messages = mutableListOf<ChannelMessage>()

    private var organizations = mutableListOf<Workgroup>()
    private var channels = mutableListOf<Channel>()

    private var current_organization = ""
    private var current_channel = ""
    private var current_dm_dest = ""

    fun set_id_values(mail: String){
        this.mail = mail
        moi_fd = File(moi_filename)
        moi_fd.writeText(this.toString())
    }

    private fun recuperate_from_file() {
        moi_fd = File(moi_filename)
        var js = JSONObject(moi_fd.readText())

        this.mail = js["mail"] as String
    }

    override fun toString(): String {
        var asdf = JSONObject()
        asdf.put("mail", mail)
        return asdf.toString()
    }

    fun save_dm(author: String, timestamp: String, body: String){
        dm_messages.add(DirectMessage(author, timestamp, body))
    }

    fun get_dms_from_author(author: String): MutableList<DirectMessage> {
        var solicited_msgs = mutableListOf<DirectMessage>()
        for (msg in dm_messages)
            if (msg.is_authored_by(author)){
                solicited_msgs.add(msg)
            }
        return solicited_msgs
    }

    fun add_channel_message(organization: String, channel: String, author: String, timestamp: String, body: String){
        channel_messages.add(ChannelMessage(organization, channel, author, timestamp, body))
    }

    fun get_messages_from_channel(organization: String, channel: String): MutableList<ChannelMessage> {
        var solicited_msgs = mutableListOf<ChannelMessage>()
        for (msg in channel_messages)
            if(msg.is_from(organization, channel))
                solicited_msgs.add(msg)
        return solicited_msgs
    }

    fun get_mail(): String {
        if (this.mail.isNotEmpty()) {
            return this.mail
        }else{
            recuperate_from_file()
            return this.mail
        }
    }

    fun save_workgroup(workgroup_name: String, workgroup_id: Int){
        organizations.add(Workgroup())
    }
    fun save_channel(organization_name: String, channel_name: String){
        channels.add(Channel(organization_name, channel_name))
    }

    fun update_current_organization(organization: String){
        current_organization = organization
        current_dm_dest = ""
    }

    fun update_current_channel(channel: String){
        current_channel = channel
        current_dm_dest = ""
    }

    fun update_current_dm_dest(dm_dest: String){
        current_dm_dest = dm_dest
        current_organization = ""
        current_channel = ""
    }

    fun get_current_organization(): String? {
        return this.current_organization
    }

    fun get_current_channel(): String? {
        return this.current_channel
    }

    fun get_current_dm_dest(): String? {
        return this.current_dm_dest
    }
}