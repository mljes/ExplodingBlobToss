package com.happycampers.explodingblobtoss

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat

class WifiPeerSetupActivity : AppCompatActivity(), WifiP2pManager.ChannelListener, DeviceListFragment.DeviceActionListener {
    companion object {
        @JvmStatic
        private val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001
    }

    private lateinit var manager: WifiP2pManager
    private var isWifiP2pEnabled = false
    private var retryChannel = false

    private lateinit var intentFilter: IntentFilter
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peer_setup)

        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET)

        getPermissions(permissionList)

        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

    }

    private fun getPermissions(permissionList: Array<String>) {
        for (permission in permissionList) {
            //get location permission from user
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(Array(2){Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION)
            }
            else {
                setIsWifiP2pEnabled(true)
            }
        }
    }

    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isWifiP2pEnabled = isWifiP2pEnabled
    }

    override fun onResume() {
        super.onResume()
        receiver = WifiDirectBroadcastReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    fun resetData() {
        val fragmentList: DeviceListFragment = supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment
        val fragmentDetails: DeviceDetailFragment = supportFragmentManager.findFragmentById(R.id.frag_detail) as DeviceDetailFragment

        if (fragmentList != null) {
            fragmentList.clearPeers()
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.atn_direct_enable -> {
                if (manager != null && channel != null) {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                else {
                    Log.e(".....", "channel or manager is null and that is BAD")
                }
                return true
            }
            R.id.atn_direct_discover -> {
                if (!isWifiP2pEnabled) {
                    Toast.makeText(this, "P2P is turned off.", Toast.LENGTH_LONG).show()
                    return true
                }

                val fragment: DeviceListFragment = supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment
                fragment.onInitiateDiscovery()
                manager.discoverPeers(channel, object: ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(this@WifiPeerSetupActivity, "STARTED PEER DISCOVERY", Toast.LENGTH_LONG).show()
                    }
                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(this@WifiPeerSetupActivity, "FAILED TO START PEER DISCOVERY", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showDetails(device: WifiP2pDevice) {
        val fragment: DeviceDetailFragment = supportFragmentManager.findFragmentById(R.id.frag_detail) as DeviceDetailFragment

        fragment.showDetails(device)
    }

    override fun connect(config: WifiP2pConfig) {
        manager.connect(channel, config, object: ActionListener {
            override fun onSuccess() {
                //ignored
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(this@WifiPeerSetupActivity, "CONNECT FAILED", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun disconnect() {
        val fragment: DeviceDetailFragment = supportFragmentManager.findFragmentById(R.id.frag_detail) as DeviceDetailFragment
        fragment.resetViews()

        manager.removeGroup(channel, object: ActionListener {
            override fun onFailure(reasonCode: Int) {
                Log.d(".....", "DISCONNECT FAILED: " + reasonCode)
            }

            override fun onSuccess() {
                fragment.view?.visibility = View.GONE
            }
        })
    }

    override fun onChannelDisconnected() {
        if (manager != null && !retryChannel) {
            Toast.makeText(this@WifiPeerSetupActivity, "Channel lost. Trying again", Toast.LENGTH_LONG).show()
            resetData()
            retryChannel = true
            manager.initialize(this@WifiPeerSetupActivity, mainLooper, this)
        }
        else {
            Toast.makeText(this, "Channel likely lost permanently.\nTry disable/re-enable P2P.", Toast.LENGTH_LONG).show()
        }
    }

    override fun cancelDisconnect() {
        if (manager != null) {
            val fragment: DeviceListFragment = supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment

            if (fragment.getDevice() == null
                || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect()
            }
            else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                manager.cancelConnect(channel, object: ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(this@WifiPeerSetupActivity, "Aborting connection", Toast.LENGTH_LONG).show()
                    }
                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(this@WifiPeerSetupActivity, "Connect abort request failed.\nReason code: " + reasonCode, Toast.LENGTH_LONG).show()
                    }
                })
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
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(".....", "Fine location permission not granted.")
                    finish()
                }
                else {
                    setIsWifiP2pEnabled(true)
                }
            }
        }
    }
}
