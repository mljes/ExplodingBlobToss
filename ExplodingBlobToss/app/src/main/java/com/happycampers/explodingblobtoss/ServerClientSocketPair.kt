package com.happycampers.explodingblobtoss

import java.net.ServerSocket
import java.net.Socket

class ServerClientSocketPair(val serverSocket: ServerSocket, val clientSocket: Socket?) {
    override fun toString(): String {
        return "[ SERVER: " + serverSocket + " | CLIENT: " + clientSocket + "]"
    }
}
