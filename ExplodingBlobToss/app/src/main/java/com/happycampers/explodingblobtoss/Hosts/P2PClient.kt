package com.happycampers.explodingblobtoss.Hosts

import android.animation.ObjectAnimator
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.happycampers.explodingblobtoss.DeviceP2PListeningState
import com.happycampers.explodingblobtoss.GameActivity
import com.happycampers.explodingblobtoss.GameActivity.Companion.blob
import com.happycampers.explodingblobtoss.GameActivity.Companion.catchAnimation
import com.happycampers.explodingblobtoss.GameActivity.Companion.catchBlob
import com.happycampers.explodingblobtoss.GameActivity.Companion.throwAnimation
import com.happycampers.explodingblobtoss.GameActivity.Companion.throwBlob
import com.happycampers.explodingblobtoss.R
import kotlinx.android.synthetic.main.activity_game.view.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.*

class P2PClient {
    companion object {
        class ClientMessageTransferTask(val address: InetAddress) : AsyncTask<Int, Void?, Void>() {
            private val TAG = "P2PClient.TRANSFER"
            private var socket: Socket? = null

            override fun onPreExecute() {

                if (GameActivity.deviceState == DeviceP2PListeningState.SENDING) {
                    GameActivity.deviceState = DeviceP2PListeningState.RECEIVING
                }
                else {
                    cancel(false) //TODO: ensure this doesn't break safe calls
                }
            }

            override fun doInBackground(vararg turnsLeft: Int?): Void? {
                sendData(address, turnsLeft[0]!!)
                return null
            }

            private fun sendData(serverAddress: InetAddress, turnsLeft: Int) {
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

                    message = "$turnsLeft THIS IS THE CLIENT'S MESSAGE"

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

            override fun onPostExecute(result: Void?) {
                throwBlob()
            }
        }

        class ClientMessageReceiveTask(private val activity: WeakReference<GameActivity>): AsyncTask<InetAddress, Void?, String>() {
            private val TAG = "P2PClient.RECEIVE"
            private var socket: Socket? = null
            private var receiverRetryCount = 0
            private var message: String? = null

            override fun doInBackground(vararg p0: InetAddress?): String? {
                var receiving = receiveData(p0[0]!!)
                while (receiving) {
                    if (message != null) {
                        return message!!
                    }

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
                        println("SOCKET WAS NOT BOUND")
                        socket?.bind(null)
                    }

                    if (socket != null && !socket!!.isConnected) {
                        println("SOCKET WAS NOT CONNECTED")
                        socket?.connect(InetSocketAddress(serverAddress, 8993))
                    }

                    if (socket!!.isConnected) {
                        println("SOCKET IS CONNECTED")
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

                        this.message = messageString

                        println(
                            "MESSAGE FROM SERVER: " + messageString
                        )

                        outputStream.flush()

                        socket!!.close()
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

            override fun onPostExecute(result: String?) {
                println("IN POST EXECUTE CLIENT RECEIVE")
                if (result != null && result.isNotEmpty()) {
                    println("GOT A MESSAGE ON THE CLIENT")
                   catchBlob(result)

                }
            }
        }
    }
}