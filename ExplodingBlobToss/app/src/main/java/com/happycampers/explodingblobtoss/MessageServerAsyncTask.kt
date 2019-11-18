package com.happycampers.explodingblobtoss

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import javax.security.auth.callback.Callback

class MessageServerAsyncTask:
    AsyncTask<Void, Void, String>() {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket

    private fun receiveData() {
        var buf = ByteArray(1024)
        var len = 0

        try {
            serverSocket = ServerSocket(8997)
            clientSocket = serverSocket.accept()
            if (isCancelled) return

            println("SERVER IS ON PORT " + serverSocket)

            val inputStream = clientSocket.getInputStream()
            val outputStream = ByteArrayOutputStream(1024)

            DeviceDetailFragment.copyMessage(inputStream, outputStream)

            //serverSocket.close()

            val messageString = outputStream.toString()

            println("MESSAGE FROM " + clientSocket.inetAddress + " : " + clientSocket.localPort
                    + messageString)
            inputStream.close()
            outputStream.close()
        }
        catch (e: Exception) {
            Log.d("MESSAGESERVERASYNCTASK", e.message)
        }
    }

    override fun doInBackground(vararg p0: Void?): String? {
        receiveData()
        return null
    }
}