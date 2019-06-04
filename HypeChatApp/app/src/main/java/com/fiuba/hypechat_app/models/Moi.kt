package com.fiuba.hypechat_app.models

import com.fiuba.hypechat_app.Workspace
import com.google.firebase.auth.FirebaseAuth


object Moi {
    /* Moi name refers to Me - myself in french. This object is intended to contain all
     * info needed to identify oneself and to make oneself useful.
     * Oneself here denotes the logged user.
     */

//    val SERVER_URL = "https://hypechatgrupo2-app-server-stag.herokuapp.com/"
    val SERVER_URL = "http://192.168.2.110:5000/"

    /* personal info */
//    private lateinit var username: String
    private lateinit var mail: String
    // private lateinit var context: Context

    private var dm_messages = mutableListOf<DirectMessage>()
    private var channel_messages = mutableListOf<ChannelMessage>()

    private var organizations = mutableListOf<Workgroup>()
    private var channels = mutableListOf<Channel>()

    private lateinit var current_organization : Workgroup
    private var current_channel_name : String = ""
    private  var current_dm_dest_name: String =""
    private  var current_channel : Channel = Channel("")
    private  var current_dm_dest : DirectMessage = DirectMessage("","","")


    fun saveDm(author: String, timestamp: String, body: String) {
        dm_messages.add(DirectMessage(author, timestamp, body))
    }

    fun getDmsFromAuthor(author: String): MutableList<DirectMessage> {
        var solicited_msgs = mutableListOf<DirectMessage>()
        for (msg in dm_messages)
            if (msg.is_authored_by(author)) {
                solicited_msgs.add(msg)
            }
        return solicited_msgs
    }

    fun addChannelMessage(organization: String, channel: String, author: String, timestamp: String, body: String) {
        channel_messages.add(ChannelMessage(organization, channel, author, timestamp, body))
    }

    fun getMessagesFromChannel(organization: String, channel: String): MutableList<ChannelMessage> {
        var solicited_msgs = mutableListOf<ChannelMessage>()
        for (msg in channel_messages)
            if (msg.is_from(organization, channel))
                solicited_msgs.add(msg)
        return solicited_msgs
    }

    fun getMail(): String {
        var mail = ""
        try {
            mail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        } catch (t: Throwable) {
            mail = ""
        }
        return mail
    }


    fun saveChannel(organization_name: String, channel_name: String) {
        channels.add(Channel(channel_name))
    }

    fun updateCurrentOrganization(organization: Workgroup) {
        current_organization = organization
        //current_dm_dest = ""
    }

    fun updateCurrentChannel(channel: Channel) {
        current_channel = channel

    }

    fun updateCurrentDmDest(dm_dest: DirectMessage) {
        current_dm_dest = dm_dest
    }

    fun updateCurrentDmDestName(dm_dest: String) {
        current_dm_dest_name = dm_dest

    }

    fun getCurrentOrganizationName(): String {
        return this.current_organization.name
    }

    fun getCurrentOrganization(): Workgroup {
        return this.current_organization
    }

    fun getCurrentChannel(): Channel? {
        return this.current_channel
    }

    fun getCurrentDmDest(): DirectMessage? {
        return this.current_dm_dest
    }

    fun getCurrentDmDestName(): String? {
        return this.current_dm_dest_name
    }

    fun saveWorkspace(workspace: Workspace) {
        channels.clear()
        workspace.channels.forEach {
            channels.add(Channel(it))
        }
    }

    fun getChannelList(): MutableList<Channel> {
        return channels
    }

    fun setCurrentChannel(channel: String) {
        current_channel_name = channel
    }

    fun addChannel(channel: Channel){
        channels.add(channel)
    }

    fun getUrlImageForCurrentOrga(): String {
        return current_organization.urlImage
    }

    fun getCurrentOrganizationsDescription(): String {
        return current_organization.description
    }
}