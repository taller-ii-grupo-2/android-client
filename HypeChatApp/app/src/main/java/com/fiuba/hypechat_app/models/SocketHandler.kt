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
        put_msg_together(msg)

        socket?.emit("message", msg)
        Log.d ("SocketHandler", msg)
    }

    private fun put_msg_together(msg: String) {
        var asdf = JSONObject()
        asdf.put("organization", "")
        asdf.put("channel", "")
        asdf.put("dm_dest", "")
        asdf.put("body", msg)
    }

    fun getSocket():Socket{
        return this.socket!!
    }
}