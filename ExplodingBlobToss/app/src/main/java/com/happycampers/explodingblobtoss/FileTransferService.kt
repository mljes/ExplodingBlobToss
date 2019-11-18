package com.happycampers.explodingblobtoss

import android.app.IntentService
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

class FileTransferService(val name: String): IntentService(name) {
    companion object {
        private val SOCKET_TIMEOUT = 5000
        val ACTION_SEND_FILE = "com.happycampers.explodingblobtoss.SEND_FILE"
        val EXTRAS_FILE_PATH = "file_url"
        val EXTRAS_GROUP_OWNER_ADDRESS = "go_host" // hub user?
        val EXTRAS_GROUP_OWNER_PORT = "go_port"
    }

    override fun onHandleIntent(intent: Intent?) {
        val context = applicationContext

        if (intent?.action.equals(ACTION_SEND_FILE)) {
            val fileUri: String? = intent?.extras?.getString(EXTRAS_FILE_PATH)
            val host: String? = intent?.extras?.getString(EXTRAS_GROUP_OWNER_ADDRESS)
            val socket: Socket = Socket()
            val port: Int = intent!!.extras!!.getInt(EXTRAS_GROUP_OWNER_PORT)
            val buf = ByteArray(1024)
            var len: Int

            try {
                socket.bind(null)
                socket.connect((InetSocketAddress(host, port)), SOCKET_TIMEOUT)

                val outputStream = socket.getOutputStream()
                val contentResolver: ContentResolver = context.contentResolver

                //TODO: put a real message in here...
                val inputStream: InputStream = ByteArrayInputStream("AN IMPORTANT MESSAGE".toByteArray())

                DeviceDetailFragment.copyMessage(inputStream, outputStream)

                outputStream.close()
                inputStream.close()


            }
            catch (e: IOException) {
                Log.e(".....", e.message)
            }
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close()
                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    }



}
