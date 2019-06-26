package com.fiuba.hypechat_app.models

import com.fiuba.hypechat_app.User

class Channel (internal var channel_name: String) {

    //private var userList = mutableListOf<User>()
    private var desc : String = ""
    private var private:Boolean = true
    private var nameOrga: String = ""
    private lateinit var invitationList : List<String>
    constructor(nameOrga:String,channel_name: String,public: Boolean,descriptionChannel: String) : this(channel_name){
        this.nameOrga = nameOrga
        this.desc = descriptionChannel
        this.private = public
        //this.invitationList = invitationList
    }


    fun isPublic (): Boolean{
        return private
    }

    fun setPublic(public: Boolean){
        this.private = public
    }
}

