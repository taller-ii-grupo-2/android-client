package com.fiuba.hypechat_app.models
import android.util.Log
import com.fiuba.hypechat_app.activities.ChatItem
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder


object SocketHandler {
    private var socket: Socket? = null

    /*
     * Para entender en @Synchronized: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-synchronized/
     */
    @Synchronized
    fun setSocket(url: String) {
        this.socket = IO.socket(url)
        socket?.connect()
    }

    @Synchronized
    fun send(msg: String) {
        socket?.emit("message", msg)
        Log.d ("SocketHandler", msg)
    }


    fun getSocket():Socket{
        return this.socket!!
    }




}