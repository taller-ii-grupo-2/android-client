package com.fiuba.hypechat_app.models

class Channel {
//    private var organization: Organization
    private var name: String
    private var id: Int

    constructor(name: String, id: Int){
//        this.organization = organization
        this.name = name
        this.id = id
    }
}