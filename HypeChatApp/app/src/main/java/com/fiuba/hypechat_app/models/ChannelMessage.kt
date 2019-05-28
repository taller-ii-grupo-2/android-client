package com.fiuba.hypechat_app.models

class ChannelMessage {
    fun is_from(organization: String, channel: String): Boolean {
        return (this.organization == organization && this.channel == channel)
    }

    private var organization: String
    private var channel: String
    private var author: String
    private var timestamp: String
    private var msg_body: String

    constructor(organization: String, channel: String, author: String, timestamp: String, msg_body: String) {
        this.organization = organization
        this.channel = channel
        this.author = author
        this.timestamp = timestamp
        this.msg_body = msg_body
    }
}