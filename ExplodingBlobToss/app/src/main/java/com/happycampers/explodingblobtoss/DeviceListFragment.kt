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
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import java.lang.Exception

class DeviceListFragment: ListFragment(), WifiP2pManager.PeerListListener{
    private var peers: MutableList<WifiP2pDevice> = ArrayList()
    lateinit var contentView: View
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
            listAdapter = WifiPeerListAdapter(this.context!!, R.layout.row_devices, peers)
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
            var view: View? = convertView

            if (view == null) {
                val viewInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                view = viewInflater.inflate(R.layout.row_devices, null)
            }

            var device: WifiP2pDevice? = null

            if (position < objects.size) {
                device = objects[position]
            }

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

            return view!!
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
        val wifiPeerListAdapter = (listAdapter as WifiPeerListAdapter)

        peers.clear()
        wifiPeerListAdapter.clear()

        for (item in peerList!!.deviceList) {
            val deviceType: String = item.primaryDeviceType

            if (deviceType.subSequence(0, deviceType.indexOf("-")).equals("10")){
                peers.add(item)
                wifiPeerListAdapter.add(item)
            }
        }

        println(peers.size)

        wifiPeerListAdapter.notifyDataSetChanged()

        if (peers.size== 0) {
            Toast.makeText(this.context, "AWAITING PEERS", Toast.LENGTH_LONG).show()
            return
        }


    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val device: WifiP2pDevice = listAdapter.getItem(position) as WifiP2pDevice
        (this.context!! as DeviceActionListener).showDetails(device)
    }

    fun clearPeers() {
        peers.clear()
        (listAdapter as WifiPeerListAdapter).notifyDataSetChanged()
    }

    fun onInitiateDiscovery() {
    }

    interface DeviceActionListener {
        fun showDetails(device: WifiP2pDevice)
        fun cancelDisconnect()
        fun connect(config: WifiP2pConfig)
        fun disconnect()
    }
}
