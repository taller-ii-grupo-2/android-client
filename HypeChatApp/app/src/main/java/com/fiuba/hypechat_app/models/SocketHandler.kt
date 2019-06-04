package com.fiuba.hypechat_app.models

import android.util.Log
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONObject


object SocketHandler {
    private var socket: Socket? = null

    /*
     * Para entender en @Synchronized: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-synchronized/
     */
    @Synchronized
    fun setSocket(url: String, mail: String) {
        this.socket = IO.socket(url)
        socket?.connect()
        socket?.emit("identification", mail)
    }

    @Synchronized
    fun send(msg: String) {
        socket?.emit("message", putMsgTogether(msg))
        Log.d("SocketHandler", msg)
    }

    private fun putMsgTogether(msg: String): String {
        val asdf = JSONObject()
        asdf.put("organization", Moi.getCurrentOrganization())
        asdf.put("channel", Moi.getCurrentChannel())
        asdf.put("dm_dest", Moi.getCurrentDmDest())
        asdf.put("body", msg)

        return asdf.toString()
    }

    fun getSocket(): Socket {
        return this.socket!!
    }

    fun disconnect() {
        socket?.disconnect()
    }
}