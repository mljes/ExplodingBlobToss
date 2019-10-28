package com.happycampers.explodingblobtoss

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

class DeviceDetailFragment: Fragment(), WifiP2pManager.ConnectionInfoListener {
    protected val CHOOSE_FILE_RESULT_CODE: Int = 20
    private lateinit var contentView: View
    private lateinit var device: WifiP2pDevice
    private var info: WifiP2pInfo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        try {
            super.onActivityCreated(savedInstanceState)
        }
        catch (e: Exception) {
            Log.e("DEVICEDETAILFRAGMENT2", e.message)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            contentView = inflater.inflate(R.layout.device_detail, null)

            contentView.findViewById<Button>(R.id.btn_connect).setOnClickListener { view: View? ->
                val config = WifiP2pConfig()
                config.deviceAddress = device.deviceAddress
                config.wps.setup = WpsInfo.PBC

                (activity as DeviceListFragment.DeviceActionListener).connect(config)
            }

            contentView.findViewById<Button>(R.id.btn_disconnect).setOnClickListener {
                (activity as DeviceListFragment.DeviceActionListener).disconnect()
            }

            contentView.findViewById<Button>(R.id.btn_start_client).setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE)
            }
        }
        catch (e: Exception) {
            Log.e("DEVICEDETAILFRAGMENT", e.message!!)
        }


        return contentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var uri: Uri? = data?.data

        val statusText = contentView.findViewById(R.id.status_text) as TextView
        statusText.text = "Sending" + uri

        val serviceIntent: Intent = Intent(activity, FileTransferService::class.java)
        serviceIntent.action = FileTransferService.ACTION_SEND_FILE
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString())
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info?.groupOwnerAddress?.hostAddress)
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988)

        activity?.startService(serviceIntent)
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        this.info = info

        this.view?.visibility = View.VISIBLE

        // known owner IP
        val ownerView: TextView = contentView.findViewById(R.id.group_owner)

        var answer = ""

        if (info!!.isGroupOwner) answer = "YES"
        else answer = "NO"

        ownerView.setText(resources.getString(R.string.group_owner_text) + answer)

        //InetAddress from WifiP2pInfo
        val deviceInfoView: TextView = contentView.findViewById(R.id.device_info)
        deviceInfoView.text = "Group Owner IP: " + info.groupOwnerAddress?.hostAddress

        if (info.groupFormed && info.isGroupOwner) {
            FileServerAsyncTask(activity!!, contentView.findViewById<TextView>(R.id.status_text)).execute()
        }
        else if (info.groupFormed) {
            contentView.findViewById<Button>(R.id.btn_start_client).visibility = View.VISIBLE
            contentView.findViewById<TextView>(R.id.status_text).text = resources.getString(R.string.client_text)
        }

        contentView.findViewById<Button>(R.id.btn_connect).visibility = View.GONE
    }

    fun showDetails(device: WifiP2pDevice) {
        this.device = device
        this.view?.visibility = View.VISIBLE

        val addressView = contentView.findViewById<TextView>(R.id.device_address)
        addressView.text = device.deviceAddress

        val infoView = contentView.findViewById<TextView>(R.id.device_info)
        infoView.text = device.toString()
    }

    fun resetViews() {
        println("IN RESETVIEWS IN DEVICEDETAILFRAGMENT")

        contentView.findViewById<Button>(R.id.btn_connect).visibility = View.VISIBLE

        contentView.findViewById<TextView>(R.id.device_address).text = ""
        contentView.findViewById<TextView>(R.id.device_info).text = ""
        contentView.findViewById<TextView>(R.id.group_owner).text = ""
        contentView.findViewById<TextView>(R.id.status_text).text = ""

        contentView.findViewById<Button>(R.id.btn_start_client).visibility = View.GONE
        this.getView()?.visibility = View.GONE
    }



    companion object {
        fun copyFile(inputStream: InputStream, out: OutputStream): Boolean {
            var buf = ByteArray(1024)

            var len: Int = inputStream.read(buf)

            try {
                while (len != -1) {
                    out.write(buf, 0, len)

                    len = inputStream.read(buf)
                }

                out.close()
                inputStream.close()
            }
            catch (e: IOException) {
                Log.d(".....", e.message)
                return false
            }

            return true
        }
    }

    class FileServerAsyncTask(val context: Context, val statusText: View):
        AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val serverSocket = ServerSocket(8988)
                Log.d(".....", "SOCKET OPENED")

                val client: Socket = serverSocket.accept()
                Log.d(".....", "SERVER CONNECTION DONE")

                val f = File(context.getExternalFilesDir("received"), "wifip2pshared-" + System.currentTimeMillis() + ".jpg")

                val dirs = File(f.parent!!)

                if (!dirs.exists()) dirs.mkdirs()

                f.createNewFile()

                val inputStream: InputStream = client.getInputStream()
                copyFile(inputStream, FileOutputStream(f))

                serverSocket.close()

                return f.absolutePath
            }
            catch (e: IOException) {
                Log.e(".....", e.message)
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                (statusText as TextView).text = "File copied - " + result

                val recvFile = File(result)

                val fileUri = FileProvider.getUriForFile(
                    context,
                    "com.happycampers.explodingblobtoss.fileprovider",
                    recvFile)

                val intent = Intent()

                intent.action = Intent.ACTION_VIEW
                intent.setDataAndType(fileUri, "image/*")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                context.startActivity(intent)
            }
        }

        override fun onPreExecute() {
            (statusText as TextView).text = "Opening a server socket"
        }
    }


}