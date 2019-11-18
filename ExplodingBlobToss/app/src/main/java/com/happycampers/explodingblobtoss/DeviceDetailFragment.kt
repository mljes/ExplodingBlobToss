package com.happycampers.explodingblobtoss

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.p2p.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class DeviceDetailFragment: Fragment(), WifiP2pManager.ConnectionInfoListener {
    protected val CHOOSE_FILE_RESULT_CODE: Int = 20
    private lateinit var contentView: View
    private lateinit var device: WifiP2pDevice
    private var info: WifiP2pInfo? = null

    private lateinit var socketPair: ServerClientSocketPair

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

                (activity as DeviceListFragment.DeviceActionListener).connect(config)
            }

            contentView.findViewById<Button>(R.id.btn_disconnect).setOnClickListener {
                (activity as DeviceListFragment.DeviceActionListener).disconnect()
            }

            contentView.findViewById<Button>(R.id.btn_start_client).setOnClickListener {
                ClientMessageTransferTask().execute(info!!.groupOwnerAddress).get()
            }
        }
        catch (e: Exception) {
            Log.e("DEVICEDETAILFRAGMENT", e.message!!)
        }


        return contentView
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
            println("This is where we are")
            socketPair = SocketSetupAsyncTask().execute(8997).get()
            //starts receiving messages
            MessageServerAsyncTask().execute()
            return
        }
        else if (info.groupFormed) {
            contentView.findViewById<Button>(R.id.btn_start_client).visibility = View.VISIBLE

            Toast.makeText(activity, "GROUP FORMED - " + info.groupOwnerAddress, Toast.LENGTH_LONG).show()
            //contentView.findViewById<TextView>(R.id.status_text).text = resources.getString(R.string.client_text)
            return
        }
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
        //contentView.findViewById<TextView>(R.id.status_text).text = ""

        contentView.findViewById<Button>(R.id.btn_start_client).visibility = View.GONE
        this.getView()?.visibility = View.GONE
    }

    companion object {
        fun copyMessage(inputStream: InputStream, out: OutputStream): Boolean {
            var buf = ByteArray(1024)

            var len: Int = inputStream.read(buf)

            try {
                while (len != -1) { //reached end of buffer
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

}