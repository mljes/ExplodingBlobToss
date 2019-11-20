package com.happycampers.explodingblobtoss.Hosts

import android.os.AsyncTask
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.*

class P2PServer {
    companion object {
        class MessageServerAsyncTask: AsyncTask<Void, Void, String>() {
            private var serverSocket: ServerSocket? = null
            private var clientSocket: Socket? = null

            private val TAG = "P2PServer.RECEIVE"

            override fun doInBackground(vararg p0: Void?): String? {
                var receiving = receiveData()
                while (receiving) {
                    receiving = receiveData()
                }
                return null
            }

            private fun receiveData(): Boolean {
                try {
                    if (serverSocket == null) {
                        serverSocket = ServerSocket(8997)
                    }

                    clientSocket = serverSocket?.accept()

                    if (isCancelled) return false

                    Log.d(TAG, "SERVER IS ON PORT " + serverSocket?.localPort)

                    val inputStream = clientSocket?.getInputStream()
                    val outputStream = ByteArrayOutputStream(32)

                    var buf = ByteArray(32)

                    var len: Int = inputStream!!.read(buf)
                    outputStream.write(buf, 0, len)

                    println(buf.toString())

                    val messageString = outputStream.toString()


                    println(
                        "MESSAGE FROM CLIENT: " + messageString
                    )

                    outputStream.flush()

                    inputStream.close()
                    outputStream.close()

                    return true
                } catch (e: Exception) {
                    Log.d(TAG, e.message)
                    return false
                }
            }
        }

        private var transferServerSocket: ServerSocket? = null
        private var transferClientSocket: Socket? = null
        val TAG_TRANSFER = "P2PServer.TRANSFER"

        class StartServerForTransferTask: AsyncTask<InetAddress, Void, Void>() {

            override fun doInBackground(vararg p0: InetAddress?): Void? {
                try {
                    openServerSocket()
                }
                catch (e: Exception) {
                    Log.d(TAG_TRANSFER, "Couldn't open server socket: " + e.toString())
                }

                return null
            }

            private fun openServerSocket(): Boolean {
                try {
                    if (transferServerSocket == null) {
                        transferServerSocket = ServerSocket(8993)
                    }

                    transferClientSocket = transferServerSocket?.accept()

                    return true
                }
                catch (e: Exception) {
                    Log.d(TAG_TRANSFER, "Could not start server socket for transferring from server: " + e.message)
                    return false
                }
            }

        }

        class ServerMessageTransferTask(val address: InetAddress): AsyncTask<InetAddress, Void?, Void>() {

            override fun doInBackground(vararg p0: InetAddress?): Void? {
                sendData(address)
                return null
            }

            private fun sendData(socketAddress: InetAddress) {
                var message = ""

                try {
                    if (transferServerSocket == null) {
                        transferServerSocket = ServerSocket(8993, 0, socketAddress)
                    }

                    Log.d(TAG_TRANSFER, "Receiving ")

                    val outputStream: OutputStream = transferClientSocket!!.getOutputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream(32)

                    message = "THIS IS THE SERVER'S MESSAGE"

                    byteArrayOutputStream.write(message.toByteArray())
                    byteArrayOutputStream.writeTo(outputStream)

                    outputStream.flush()
                    byteArrayOutputStream.flush()

                } catch (e: java.lang.Exception) {
                    Log.d("CLIENTTRANSFERSERVICE", e.toString())
                }
            }
        }
    }
}