package com.fiuba.hypechat_app.models

import com.fiuba.hypechat_app.User

class Channel (internal var channel_name: String) {

    //private var userList = mutableListOf<User>()
    private var desc : String = ""
    private var public:Boolean = true
    private var nameOrga: String = ""
    private lateinit var invitationList : List<String>
    constructor(nameOrga:String,channel_name: String,public: Boolean,descriptionChannel: String,invitationList:List<String>) : this(channel_name){
        this.nameOrga = nameOrga
        this.desc = descriptionChannel
        this.public = public
        this.invitationList = invitationList
    }


    fun isPublic (): Boolean{
        return public
    }

    fun setPublic(public: Boolean){
        this.public = public
    }
}

