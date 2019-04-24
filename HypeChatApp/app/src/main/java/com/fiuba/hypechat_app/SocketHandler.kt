package com.fiuba.hypechat_app

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.util.*

/* object como version Kotlin de class con metodos static. */
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
        socket?.send(msg)
    }
}