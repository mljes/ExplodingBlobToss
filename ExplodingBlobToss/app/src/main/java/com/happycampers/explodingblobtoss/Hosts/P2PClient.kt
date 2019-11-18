package com.happycampers.explodingblobtoss.Hosts

import android.os.AsyncTask
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.*

class P2PClient {
    companion object {
        class ClientMessageTransferTask : AsyncTask<InetAddress, Void?, Void>() {
            private var socket: Socket? = null

            override fun doInBackground(vararg p0: InetAddress?): Void? {
                sendData(p0[0]!!)
                return null
            }

            private fun sendData(serverAddress: InetAddress) {
                println("just inside sendData")
                if (socket == null) {
                    socket = Socket()
                }

                var message = ""

                try {
                    println("inside try for sendData (client)")
                    if (socket != null && !socket!!.isBound) {
                        socket?.bind(null)
                        println("bound client socket for transfer")
                    }

                    if (socket != null && !socket!!.isConnected) {
                        socket?.connect(InetSocketAddress(serverAddress, 8997))
                        println("connected client socket for transfer")
                    }

                    println("got past socket.connect (client)")

                    var outputStream: OutputStream = socket!!.getOutputStream()
                    var byteArrayOutputStream = ByteArrayOutputStream(32)

                    println("got past outputstream creation")

                    message = "THIS IS MY MESSAGE"

                    byteArrayOutputStream.write(message.toByteArray())
                    byteArrayOutputStream.writeTo(outputStream)

                    println("wrote to the outputstream")

                    outputStream.flush()
                    byteArrayOutputStream.flush()

                    byteArrayOutputStream.close()
                    outputStream.close()
                    socket!!.close()
                } catch (e: Exception) {
                    Log.d("CLIENTTRANSFER", e.toString())
                }
            }
        }

        class ClientMessageReceiveTask : AsyncTask<InetAddress, Void?, Void>() {
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
                        println("bound client socket for receive")
                    }

                    if (socket != null && !socket!!.isConnected) {
                        socket?.connect(InetSocketAddress(serverAddress, 8995))
                        println("connected client socket for receive")
                    }

                    if (socket!!.isConnected) {
                        val inputStream = socket?.getInputStream()
                        val outputStream = ByteArrayOutputStream(32)

                        println("set up streams for receive")

                        var buf = ByteArray(32)
                        var len: Int = inputStream!!.read(buf)

                        if (len < 1) {
                            return true
                        }

                        outputStream.write(buf, 0, len)

                        println(buf.toString())

                        println("PAST COPYMESSAGE IN RECEIVE")
                        //serverSocket.close()

                        val messageString = outputStream.toString()


                        println(
                            "MESSAGE FROM SERVER: " + messageString
                        )

                        outputStream.flush()

                        //inputStream.close()
                        //outputStream.close()
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
                    Log.d("CLIENTRECEIVER", e.toString())
                    return false
                }
            }
        }
    }
}