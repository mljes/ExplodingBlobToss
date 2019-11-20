package com.happycampers.explodingblobtoss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast


class WifiDirectBroadcastReceiver constructor(
    val manager: WifiP2pManager,
    val channel: WifiP2pManager.Channel,
    val activity: WifiPeerSetupActivity)
    : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        activity.setIsWifiP2pEnabled(true)
                    }
                    else -> {
                        Toast.makeText(activity, "YOU DO NOT HAVE P2P ENABLED. BYE.", Toast.LENGTH_LONG).show()
                        activity.setIsWifiP2pEnabled(false)
                        activity.resetData()
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if (manager != null) {
                    manager.requestPeers(channel, activity.supportFragmentManager.findFragmentById(com.happycampers.explodingblobtoss.R.id.frag_list) as WifiP2pManager.PeerListListener)
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                if (manager == null) {
                    return
                }

                val fragment = activity.supportFragmentManager.findFragmentById(com.happycampers.explodingblobtoss.R.id.frag_detail) as DeviceDetailFragment
                manager.requestConnectionInfo(channel, fragment)

                Toast.makeText(activity, "CONNECTION STATE HAS CHANGED", Toast.LENGTH_LONG).show()
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                var fragment: DeviceListFragment = activity.supportFragmentManager.findFragmentById(com.happycampers.explodingblobtoss.R.id.frag_list) as DeviceListFragment

                fragment.updateThisDevice(intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice)
            }
        }
    }


}