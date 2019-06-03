package com.fiuba.hypechat_app.models

import com.fiuba.hypechat_app.Workspace
import com.google.firebase.auth.FirebaseAuth


object Moi {
    /* Moi name refers to Me - myself in french. This object is intended to contain all
     * info needed to identify oneself and to make oneself useful.
     * Oneself here denotes the logged user.
     */

    val SERVER_URL = "https://hypechatgrupo2-app-server-stag.herokuapp.com/"
    //val SERVER_URL = "http://192.168.2.110:5000/"

    /* personal info */
//    private lateinit var username: String
    private lateinit var mail: String
    // private lateinit var context: Context

    private var dm_messages = mutableListOf<DirectMessage>()
    private var channel_messages = mutableListOf<ChannelMessage>()

    private var organizations = mutableListOf<Workgroup>()
    private var channels = mutableListOf<Channel>()

    private var current_organization_name : String = ""
    private  var current_organization : Workgroup = Workgroup()
    private  var current_channel : Channel = Channel("","")
    private  var current_dm_dest : DirectMessage = DirectMessage("","","")


    fun save_dm(author: String, timestamp: String, body: String) {
        dm_messages.add(DirectMessage(author, timestamp, body))
    }

    fun get_dms_from_author(author: String): MutableList<DirectMessage> {
        var solicited_msgs = mutableListOf<DirectMessage>()
        for (msg in dm_messages)
            if (msg.is_authored_by(author)) {
                solicited_msgs.add(msg)
            }
        return solicited_msgs
    }

    fun add_channel_message(organization: String, channel: String, author: String, timestamp: String, body: String) {
        channel_messages.add(ChannelMessage(organization, channel, author, timestamp, body))
    }

    fun get_messages_from_channel(organization: String, channel: String): MutableList<ChannelMessage> {
        var solicited_msgs = mutableListOf<ChannelMessage>()
        for (msg in channel_messages)
            if (msg.is_from(organization, channel))
                solicited_msgs.add(msg)
        return solicited_msgs
    }

    fun get_mail(): String {
        var mail = ""
        try {
            mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        } catch (t: Throwable) {
            mail = ""
        }
        return mail
    }


    fun save_channel(organization_name: String, channel_name: String) {
        channels.add(Channel(organization_name, channel_name))
    }

    fun update_current_organization_name(organization: String) {
        current_organization_name = organization
        //current_dm_dest = ""
    }
    fun update_current_organization(organization: Workgroup) {
        current_organization= organization
        //current_dm_dest = ""
    }

    fun update_current_channel(channel: Channel) {
        current_channel = channel
        //current_dm_dest = ""
    }

    fun update_current_dm_dest(dm_dest: DirectMessage) {
        current_dm_dest = dm_dest
        current_organization_name = ""
        //current_channel =""
    }

    fun get_current_organization_name(): String {
        return this.current_organization_name
    }

    fun get_current_organization(): Workgroup {
        return this.current_organization
    }

    fun get_current_channel(): Channel? {
        return this.current_channel
    }

    fun get_current_dm_dest(): DirectMessage? {
        return this.current_dm_dest
    }

    fun save_workspace(workspace: Workspace) {
        workspace.channels.forEach {
            channels.add(Channel(current_organization_name,it))
        }

        organizations.add(Workgroup(current_organization_name,workspace.description,workspace.welcomMsg,workspace.urlImage))

        this.update_current_organization(organizations.get(organizations.lastIndex))
    }

    fun get_channel_list(): MutableList<Channel> {
        return channels
    }

    fun set_current_channel(channel: String) {

        channels.forEach {
            if (it.nameOrga == channel)
                current_channel = it;
        }
    }
}