package com.happycampers.explodingblobtoss

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    /*
    private val PERMISSION_REQUEST_CODE = 100
    private val intentFilter = IntentFilter()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var receiver: WifiDirectBroadcastReceiver

    private lateinit var deviceListRecyclerView: RecyclerView
    private lateinit var peerRecyclerViewAdapter: RecyclerView.Adapter<*>
    var isWifiP2pEnabled = false

    private val peers = mutableListOf<WifiP2pDevice>()

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
    }
}
/*
        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET)

        getPermissions(permissionList)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        //this needs to be at the bottom for some reason
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        peerRecyclerViewAdapter = MyAdapter(myDataset)

        //start discovery process (doesn't actually find any peers)
        manager.discoverPeers(channel, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                //TODO: do something when peers discovery OK
                println("The device is now listening for peers.")
            }
            override fun onFailure(reasonCode: Int) {
                //TODO: do something when peer discovery fails
                println("Peer discovery did not start successfully")
            }
        })

        deviceListRecyclerView = findViewById<RecyclerView>(R.id.peerListRecycler).apply {
            setHasFixedSize(true)
        }
    }

    public override fun onResume() {
        super.onResume()
        receiver = WifiDirectBroadcastReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            //TODO: make a list adapter to display the list of peers
        }

        if (peers.isEmpty()) {
            Log.d("ERROR", "No devices found")
            return@PeerListListener
        }
    }

    fun getPermissions(permissionList: Array<String>) {
        for (permission in permissionList) {
            //get location permission from user
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,  Array<String>(2){permission}, 100)
            }
        }
    }

    /**
     * Checks for permission to use location. If location not granted, removes weather by location button.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //TODO: not sure yet... disable stuff I guess...
                    return
                } else {
                    //TODO: commence
                }
            }
        }
    }
}


 */