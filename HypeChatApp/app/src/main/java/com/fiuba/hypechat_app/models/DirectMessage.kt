package com.fiuba.hypechat_app.models

import java.sql.Timestamp

class DirectMessage {
    private var author: String
    private var timestamp: String
    private var msg_body: String

    constructor(author: String, timestamp: String, msg_body: String){
        this.author = author
        this.timestamp = timestamp
        this.msg_body = msg_body
    }

    fun is_authored_by(author: String): Boolean {
        return this.author == author
    }
}