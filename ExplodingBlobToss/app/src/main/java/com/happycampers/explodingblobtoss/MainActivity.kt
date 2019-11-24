package com.happycampers.explodingblobtoss

import android.Manifest
import android.app.PendingIntent.getActivity
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
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        val actionBar = supportActionBar
        actionBar?.hide()
        actionBar?.setDisplayShowHomeEnabled(true)
         actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.main_menu)

        val startButton = findViewById<Button>(R.id.start_btn)
        startButton.setOnClickListener {
            val intent = Intent(this, WifiPeerSetupActivity::class.java).apply {
                //?
            }
            startActivity(intent)
        }
    }
}
