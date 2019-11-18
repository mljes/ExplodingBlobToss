package com.happycampers.explodingblobtoss

import android.content.ContentResolver
import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.AsyncTask
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientMessageTransferTask: AsyncTask<InetAddress, Void?, Socket>() {

    override fun doInBackground(vararg p0: InetAddress?): Socket? {
        val clientSocket = sendData(p0[0]!!)

        if (clientSocket == null) {
            throw Exception("Failure in socket binding or connection.")
        }
        else {
            return clientSocket
        }
    }

    private fun sendData(serverAddress: InetAddress): Socket? {
        var len = 0
        var buf = ByteArray(1024)

        val socket = Socket()

        var message = ""

        try {
            socket.bind(null)

            socket.connect(InetSocketAddress(serverAddress, 8997))

            Log.d("DEVICEDETAILFRAGMENT", "got past socket.connect")
            println("got past socket.connect")

            var outputStream: OutputStream = socket.getOutputStream()
            var byteArrayOutputStream = ByteArrayOutputStream(1024)
            Log.d("DEVICEDETAILFRAGMENT", "got past outputstream creation")
            println("got past outputstream creation")

            message = "THIS IS MY MESSAGE"

            byteArrayOutputStream.write(message.toByteArray())
            byteArrayOutputStream.writeTo(outputStream)

            Log.d("DEVICEDETAILFRAGMENT", "wrote to the outputstream")
            println("wrote to the outputstream")

            //byteArrayOutputStream.close()
           // outputStream.close()
           // socket.close()

            Log.d("DEVICEDETAILFRAGMENT", "closed all streams")

            return socket
        }
        catch(e: Exception) {
            Log.d("P2PRANSFERSERVICE", e.toString())

            return null
        }
    }
}