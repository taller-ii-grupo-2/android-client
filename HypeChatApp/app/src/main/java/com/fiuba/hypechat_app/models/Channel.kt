package com.fiuba.hypechat_app.models

import com.fiuba.hypechat_app.User

class Channel (internal var channel_name: String) {

    private var userList = mutableListOf<User>()
    private var descriptionChannel : String = ""
    private var public:Boolean = true

    constructor(nameOrga:String,channel_name: String,public: Boolean,desc: String) : this(channel_name){
        descriptionChannel = desc
        this.public = public

    }


    fun isPublic (): Boolean{
        return public
    }

    fun setPublic(public: Boolean){
        this.public = public
    }
}