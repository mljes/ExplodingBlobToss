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

                    println("SERVER IS ON PORT " + serverSocket)

                    val inputStream = clientSocket?.getInputStream()
                    val outputStream = ByteArrayOutputStream(32)

                    println("STREAMS CREATED")

                    println("In copy message")
                    var buf = ByteArray(32)

                    var len: Int = inputStream!!.read(buf)
                    outputStream.write(buf, 0, len)

                    println(buf.toString())

                    println("PAST COPYMESSAGE")
                    //serverSocket.close()

                    val messageString = outputStream.toString()


                    println(
                        "MESSAGE FROM CLIENT: " + messageString
                    )

                    outputStream.flush()

                    inputStream.close()
                    outputStream.close()

                    return true
                } catch (e: Exception) {
                    Log.d("MESSAGESERVERASYNCTASK", e.message)
                    return false
                }
            }
        }

        private var transferServerSocket: ServerSocket? = null
        private var transferClientSocket: Socket? = null

        class StartServerForTransferTask(val address: InetAddress): AsyncTask<InetAddress, Void, Void>() {
            override fun doInBackground(vararg p0: InetAddress?): Void? {
                try {
                    Log.d("TRANSFERSERVER", "start server is here *****")
                    //var keepSocketAlive = openServerSocket(address)
                    //while (keepSocketAlive) {
                        openServerSocket(address)
                    //}
                }
                catch (e: Exception) {
                    Log.d("TRANSFERSERVER", "$$$$$$$ CRAHSED " + e.toString())
                }

                return null
            }

            override fun onPreExecute() {
                Log.d("TRANSFERSERVER", "In PREEXECUTE FOR TRANSEFER SERV")
            }

            private fun openServerSocket(socketAddress: InetAddress): Boolean {
                try {
                    println("before setting server socket")
                    if (transferServerSocket == null) {
                        transferServerSocket = ServerSocket(8993)
                    }

                    println("IN OPENSERVERSOCKET")

                    transferClientSocket = transferServerSocket?.accept()

                    println("STREAMS CREATED")

                    return true
                }
                catch (e: Exception) {
                    println("Could not start server socket for transferring from server: " + e.message)
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

                    //transferClientSocket = transferServerSocket?.accept()

                    println("got past socket.connect")

                    val outputStream: OutputStream = transferClientSocket!!.getOutputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream(32)

                    println("got past outputstream creation")

                    message = "THIS IS THE SERVER'S MESSAGE"

                    byteArrayOutputStream.write(message.toByteArray())
                    byteArrayOutputStream.writeTo(outputStream)

                    println("wrote to the outputstream")

                    outputStream.flush()
                    byteArrayOutputStream.flush()

                    //byteArrayOutputStream.close()
                    //outputStream.close()
                    //transferClientSocket!!.close()
                    //transferServerSocket!!.close()
                } catch (e: java.lang.Exception) {
                    Log.d("CLIENTTRANSFERSERVICE", e.toString())
                }
            }
        }
    }
}