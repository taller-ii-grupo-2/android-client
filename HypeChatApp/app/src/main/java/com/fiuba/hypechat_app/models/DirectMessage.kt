package com.fiuba.hypechat_app.models

import java.sql.Timestamp

class DirectMessage {
    private var orga: String
    private var dm_dest: String
    private var author: String
    private var timestamp: String
    private var msg_body: String

    constructor(orga: String, dm_dest: String, author: String, timestamp: String, msg_body: String) {
        this.orga = orga
        this.dm_dest = dm_dest
        this.author = author
        this.timestamp = timestamp
        this.msg_body = msg_body
    }

    fun is_authored_by(author: String): Boolean {
        return this.author == author
    }

    fun if_from_orga(organization: String): Boolean {
        return this.orga == organization
    }
}