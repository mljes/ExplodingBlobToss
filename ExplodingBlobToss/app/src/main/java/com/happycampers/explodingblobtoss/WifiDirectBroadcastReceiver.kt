package com.happycampers.explodingblobtoss

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiDirectBroadcastReceiver(manager: WifiP2pManager, channel: WifiP2pManager.Channel, context: Context): BroadcastReceiver() {
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                //MainActivity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                //peer list changed
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                //connection state changed
                //manager?.requestPeers(channel, peerListListener)
                Log.d(TAG, "P2P peers changed")
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

            }
        }
    }
}