package com.happycampers.explodingblobtoss

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import java.lang.Exception

class DeviceListFragment: ListFragment(), WifiP2pManager.PeerListListener{
    private var peers: MutableList<WifiP2pDevice> = ArrayList()
    private lateinit var contentView: View
    private lateinit var device: WifiP2pDevice

    companion object {
        private fun getDeviceStatus(deviceStatus: Int): String {
            when (deviceStatus) {
                WifiP2pDevice.AVAILABLE -> return "Available"
                WifiP2pDevice.INVITED -> return "Invited"
                WifiP2pDevice.CONNECTED -> return "Connected"
                WifiP2pDevice.FAILED -> return "Failed"
                WifiP2pDevice.UNAVAILABLE -> return "Unavailable"
                else -> return "Unknown"
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        try {
            super.onActivityCreated(savedInstanceState)
            this.listAdapter = WifiPeerListAdapter(this.context!!, R.layout.row_devices, peers)
        }
        catch (e: Exception){
            Log.e("DEVICELISTFRAGMENT_ACT", e.message)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            contentView = inflater.inflate(R.layout.device_list, null)
        }
        catch (e: Exception) {
            Log.e("DEVICELISTFRAGMENT", e.message)
        }

        return contentView
    }

    fun getDevice(): WifiP2pDevice {
        return device
    }

    private class WifiPeerListAdapter(
        context: Context,
        textViewResourceId: Int,
        val objects: List<WifiP2pDevice>
    ): ArrayAdapter<WifiP2pDevice>(context, textViewResourceId) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            println("******************IN GETVIEW***********************")
            var view: View = convertView!!

            if (view == null) {
                val viewInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                view = viewInflater.inflate(R.layout.row_devices, null)
            }

            val device: WifiP2pDevice = objects[position]

            if (device != null) {
                val top: TextView = view!!.findViewById(R.id.device_name)
                val bottom: TextView = view!!.findViewById(R.id.device_details)

                if (top != null) {
                    top.text = device.deviceName
                }
                if (bottom != null) {
                    bottom.text = getDeviceStatus(device.status)
                }
            }

            return view
        }
    }

    fun updateThisDevice(device: WifiP2pDevice) {
        this.device = device

        val nameView: TextView = contentView.findViewById(R.id.my_name) as TextView
        nameView.setText(device.deviceName)

        val statusView: TextView = contentView.findViewById(R.id.my_status) as TextView
        statusView.setText(getDeviceStatus(device.status))
    }

    override fun onPeersAvailable(peerList: WifiP2pDeviceList?) {
        println("IN ONPEERSAVAILABLE")
        peers.clear()

        for (item in peerList!!.deviceList) {
            peers.add(item)
            println("PEER: " + item.deviceName + " IS A " + item.primaryDeviceType)
        }

        println(peers.size)

        (listAdapter as WifiPeerListAdapter).notifyDataSetChanged()

        if (peers.size== 0) {
            Toast.makeText(this.context, "NO PEERS", Toast.LENGTH_LONG).show()
            return
        }
    }

    fun clearPeers() {
        peers.clear()
        (listAdapter as WifiPeerListAdapter).notifyDataSetChanged()
    }

    fun onInitiateDiscovery() {
        println("PEER LIST ON DISCOVERY: " )
    }

    interface DeviceActionListener {
        fun showDetails(device: WifiP2pDevice)
        fun cancelDisconnect()
        fun connect(config: WifiP2pConfig)
        fun disconnect()
    }
}
