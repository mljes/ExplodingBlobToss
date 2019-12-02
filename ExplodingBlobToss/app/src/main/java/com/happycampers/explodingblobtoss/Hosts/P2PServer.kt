package com.happycampers.explodingblobtoss.Hosts

import android.os.AsyncTask
import android.util.Log
import com.happycampers.explodingblobtoss.GameActivity
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.net.*

class P2PServer {
    companion object {
        class MessageServerAsyncTask(private val activity: WeakReference<GameActivity>): AsyncTask<Void, Void, String>() {
            private val TAG = "P2PServer.RECEIVE"
            private var serverSocket: ServerSocket? = null
            private var clientSocket: Socket? = null
            private var message: String? = null

            override fun doInBackground(vararg p0: Void?): String? {
                var receiving = receiveData()
                while (receiving) {
                    Log.d(TAG, "BEFORE THE IF")
                    if (message != null) {
                        Log.d(TAG, "GOT THE MESSAGE SHOULD RETURN")
                        return message!!
                    }

                    receiving = receiveData()
                }
                return null
            }

            private fun receiveData(): Boolean {
                try {
                    serverSocket ?: run {
                        serverSocket = ServerSocket(8997)
                    }

                    serverSocket?.reuseAddress
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
                        "MESSAGE FROM CLIENT: $messageString"
                    )

                    this.message = messageString

                    outputStream.flush()

                    Log.d(TAG, "AFTER THE BUFFER FLUSH")
                    inputStream.close()
                    outputStream.close()
                    serverSocket!!.close()

                    return true
                } catch (e: Exception) {
                    Log.d(TAG, "Could not receive data: " + e.message)
                   return false
                }
            }

            override fun onPostExecute(result: String?) {
                println("IN POST EXECUTE (SERVER RECIEVE)")
                if (result != null && result.isNotEmpty()) {
                    println("GOT MESSAGE ON SERVER")

                    val messageCode = result.split(" ", ignoreCase = true, limit = 0)[0].toInt()

                    if (messageCode == -2) {
                        activity.get()!!.startGameEndActivity(true, "You win!\nYour opponent has dropped the blob!")
                    }
                    else {
                        activity.get()!!.catchBlob(messageCode)
                    }
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
                    Log.d(TAG_TRANSFER, "Couldn't open server socket: $e")
                }

                return null
            }

            private fun openServerSocket(): Boolean {
                try {
                    if (transferServerSocket == null) {
                        transferServerSocket = ServerSocket(8993)
                    }


                    transferServerSocket?.reuseAddress
                    transferClientSocket = transferServerSocket?.accept()

                    return true
                }
                catch (e: Exception) {
                    Log.d(TAG_TRANSFER, "Could not start server socket for transferring from server: " + e.message)
                    return false
                }
            }

        }

        class ServerMessageTransferTask(val address: InetAddress, val activity: WeakReference<GameActivity>): AsyncTask<Int, Void?, Void>() {
            override fun doInBackground(vararg turnsLeft: Int?): Void? {
                sendData(address, turnsLeft[0]!!)
                return null
            }

            private fun sendData(socketAddress: InetAddress, turnsLeft: Int) {
                var message = ""

                try {
                    if (transferServerSocket == null) {
                        transferServerSocket = ServerSocket(8993, 0, socketAddress)
                    }

                    Log.d(TAG_TRANSFER, "IS SOCKET BOUND: " + transferServerSocket!!.isBound)

                    Log.d(TAG_TRANSFER, "Sending ")

                    val outputStream: OutputStream = transferClientSocket!!.getOutputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream(32)

                    Log.d(TAG_TRANSFER, "AFTER OUTPUTSTREAMS")

                    message = "$turnsLeft THIS IS THE SERVER'S MESSAGE"

                    Log.d(TAG_TRANSFER, "AFTER SETTING MESSSAGE")

                    byteArrayOutputStream.write(message.toByteArray())
                    byteArrayOutputStream.writeTo(outputStream)

                    Log.d(TAG_TRANSFER, "AFTER WRITING TO OUTPUT STREAMS")

                    outputStream.flush()
                    byteArrayOutputStream.flush()

                    Log.d(TAG_TRANSFER, "AFTER FLUSHING OUTPUT STREAMS")
                    outputStream.close()
                    byteArrayOutputStream.close()

                } catch (e: java.lang.Exception) {
                    Log.d("SERVERTRANSFERSERVICE", e.toString())

                }
            }

            override fun onPostExecute(result: Void?) {
                activity.get()!!.throwBlob()
            }
        }
    }
}
