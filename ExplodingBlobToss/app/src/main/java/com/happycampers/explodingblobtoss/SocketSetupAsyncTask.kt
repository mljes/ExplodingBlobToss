package com.happycampers.explodingblobtoss

import android.os.AsyncTask
import java.net.ServerSocket

class SocketSetupAsyncTask:
    AsyncTask<Int, Void, ServerClientSocketPair>() {

    override fun doInBackground(vararg p0: Int?): ServerClientSocketPair? {
        try {
            val serverSocket = ServerSocket(8997)

            println("GOT PAST SERVERSOCKET")

            //val clientSocket = serverSocket.accept()

            println("Set up the sockets, should be ready to return...?")

            return ServerClientSocketPair(serverSocket, null)
        }
        catch (e: Exception) {
            println("failure in SocketSetup: " + e.message)
            return null
        }

    }


}