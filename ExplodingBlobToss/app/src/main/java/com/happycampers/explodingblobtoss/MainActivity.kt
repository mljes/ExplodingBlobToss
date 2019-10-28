package com.happycampers.explodingblobtoss

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Button;
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        val startButton = findViewById<Button>(R.id.start_btn)
        startButton.setOnClickListener {
            val intent = Intent(this, WifiPeerSetupActivity::class.java).apply {
                //stuff?
            }
            startActivity(intent)
        }
    }
}
