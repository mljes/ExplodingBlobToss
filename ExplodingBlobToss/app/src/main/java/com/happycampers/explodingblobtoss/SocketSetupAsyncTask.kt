package com.happycampers.explodingblobtoss

import android.os.AsyncTask
import java.net.ServerSocket

class SocketSetupAsyncTask:
    AsyncTask<Int, Void, ServerClientSocketPair>() {

    override fun doInBackground(vararg p0: Int?): ServerClientSocketPair? {
        val serverSocket = ServerSocket(8997)
        val clientSocket = serverSocket.accept()

        return ServerClientSocketPair(serverSocket, clientSocket)
    }


}