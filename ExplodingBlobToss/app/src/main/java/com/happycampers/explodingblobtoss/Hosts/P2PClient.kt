package com.happycampers.explodingblobtoss.Hosts

import android.os.AsyncTask
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.*

class P2PClient {
    companion object {
        class ClientMessageTransferTask(val address: InetAddress) : AsyncTask<InetAddress, Void?, Void>() {
            private val TAG = "P2PClient.TRANSFER"
            private var socket: Socket? = null

            override fun doInBackground(vararg p0: InetAddress?): Void? {
                sendData(address)
                return null
            }

            private fun sendData(serverAddress: InetAddress) {
                if (socket == null) {
                    socket = Socket()
                }

                var message = ""

                try {
                    if (socket != null && !socket!!.isBound) {
                        socket?.bind(null)
                    }

                    if (socket != null && !socket!!.isConnected) {
                        socket?.connect(InetSocketAddress(serverAddress, 8997))
                    }

                    var outputStream: OutputStream = socket!!.getOutputStream()
                    var byteArrayOutputStream = ByteArrayOutputStream(32)

                    message = "THIS IS MY MESSAGE"

                    byteArrayOutputStream.write(message.toByteArray())
                    byteArrayOutputStream.writeTo(outputStream)

                    outputStream.flush()
                    byteArrayOutputStream.flush()

                    byteArrayOutputStream.close()
                    outputStream.close()
                    socket!!.close()
                } catch (e: Exception) {
                    Log.d(TAG, e.toString())
                }
            }
        }

        class ClientMessageReceiveTask : AsyncTask<InetAddress, Void?, Void>() {
            private val TAG = "P2PClient.RECEIVE"
            private var socket: Socket? = null
            private var receiverRetryCount = 0

            override fun doInBackground(vararg p0: InetAddress?): Void? {
                var receiving = receiveData(p0[0]!!)
                while (receiving) {
                    receiving = receiveData(p0[0]!!)
                }
                return null
            }

            private fun receiveData(serverAddress: InetAddress): Boolean {
                if (socket == null) {
                    socket = Socket()
                }

                try {
                    if (socket != null && !socket!!.isBound) {
                        socket?.bind(null)
                    }

                    if (socket != null && !socket!!.isConnected) {
                        socket?.connect(InetSocketAddress(serverAddress, 8993))
                    }

                    if (socket!!.isConnected) {
                        val inputStream = socket?.getInputStream()
                        val outputStream = ByteArrayOutputStream(32)

                        var buf = ByteArray(32)
                        var len: Int = inputStream!!.read(buf)

                        if (len < 1) {
                            return true
                        }

                        outputStream.write(buf, 0, len)

                        println(buf.toString())

                        val messageString = outputStream.toString()


                        println(
                            "MESSAGE FROM SERVER: " + messageString
                        )

                        outputStream.flush()
                    }
                    return true

                }
                catch (e: ConnectException) {
                    if (receiverRetryCount < 5) {
                        receiverRetryCount++
                        return true
                    }
                    else {
                        throw e
                    }
                }
                catch (e: Exception) {
                    Log.d(TAG, e.toString())
                    return false
                }
            }
        }
    }
}