package com.fiuba.hypechat_app.models

class Channel {
//    private var organization: Organization
    private var name: String

    constructor(organization_name: String, channel_name: String){
//        this.organization = organization
        this.name = channel_name
    }
}