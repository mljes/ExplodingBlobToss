package com.happycampers.explodingblobtoss

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import com.happycampers.explodingblobtoss.Hosts.P2PServer
import kotlinx.android.synthetic.main.activity_peer_setup.*




class WifiPeerSetupActivity : AppCompatActivity(), WifiP2pManager.ChannelListener, DeviceListFragment.DeviceActionListener, WifiP2pManager.ConnectionInfoListener  {
    companion object {
        @JvmStatic
        private val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001

        lateinit var deviceToPair: WifiP2pDevice
    }

    private var isWifiP2pEnabled = false
    private var retryChannel = false
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var intentFilter: IntentFilter
    private lateinit var receiver: BroadcastReceiver
    private var info: WifiP2pInfo? = null
    private var isFirstRound: Boolean? = null
    lateinit var connectButton: Button

    private var task: P2PServer.Companion.StartServerForTransferTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peer_setup)

        isFirstRound = intent.getBooleanExtra("IS_FIRST_ROUND", false)

        //back button on actionbar
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        connectButton = findViewById(R.id.btn_connect)
        connectButton.isEnabled = false

        //btn animations
        val animation: Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
        btn_connect.startAnimation(animation)

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

        //connect button
        findViewById<Button>(R.id.btn_connect).setOnClickListener { view: View? ->
            view?.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            val config = WifiP2pConfig()
            config.deviceAddress = deviceToPair.deviceAddress

            this.connect(config)
        }

        discoverPeers()
    }

    override fun onStart() {
        super.onStart()

        if (!isFirstRound!!) {
            disconnect()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    fun discoverPeers() {
        val discoverPeersBtn = findViewById(R.id.atn_direct_discover) as ImageView
        val rotateAnimation: Animation = AnimationUtils.loadAnimation(this,R.anim.rotate)
        discoverPeersBtn.setOnClickListener {
            discoverPeersBtn.startAnimation(rotateAnimation)
            discoverPeersBtn.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )

            if (!isWifiP2pEnabled) {
                Toast.makeText(this, "Please enable Wifi-Direct", Toast.LENGTH_LONG).show()

            }else {

                manager.discoverPeers(channel, object : ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@WifiPeerSetupActivity,
                            "Searching for players",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(
                            this@WifiPeerSetupActivity,
                            "Failed to find any nearby players",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_items, menu)
        return true
    }

    //enable p2p and scan for peers buttons
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
        }

        return super.onOptionsItemSelected(item)
    }

    override fun connect(config: WifiP2pConfig) {
        manager.connect(channel, config, object: ActionListener {
            override fun onSuccess() {

            }
            override fun onFailure(reason: Int) {
                Toast.makeText(this@WifiPeerSetupActivity, "Failed to connect to player", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun disconnect() {
        manager.removeGroup(channel, object: ActionListener {
            override fun onFailure(reasonCode: Int) {
                Log.d(".....", "DISCONNECT FAILED: " + reasonCode)
            }


            override fun onSuccess() {
                //fragment.view?.visibility = View.GONE
                Log.d("WIfiP2PSetup", "Success on disconnect")
            }
        })
    }

    override fun onChannelDisconnected() {
        if (manager != null && !retryChannel) {
            Log.e("onChannelDisconnected", "Channel lost. Trying again")
            discoverPeers()
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

            if (fragment == null
                || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect()
            }
            else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                manager.cancelConnect(channel, object: ActionListener {
                    override fun onSuccess() {
                        Log.e("DisconnectOnSuccess", "Aborting connection")
                    }
                    override fun onFailure(reasonCode: Int) {
                        Log.e("DisconnectOnFailure", "Connect abort request failed.\nReason code: $reasonCode")
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

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        this.info = info

        println("IN ONCONNECTIONINFOAVAIL")

        info ?: return

        println("GROUPEFORMED: " + info.groupFormed)

        if (info.groupFormed && info.isGroupOwner) {
            Log.d("WifiPeerSetup", "THIS DEVICE IS THE SERVER/OWNER/PLAYER1")

            startGameActivity(info)
        }
        else if (info.groupFormed) {
            Log.d("WifiPeerSetup","THIS DEVICE IS THE CLIENT/PLAYER2")

            println("IN THE CLIENT CALL")

            startGameActivity(info)
        }

        println("GOT ALL THE WAY PAST THE IFS")
    }

    override fun onStop() {
        super.onStop()

        try {
            unregisterReceiver(receiver)
        }
        catch (e:Exception) {
            Log.e("WifiPeerSetup", "Could not unregister receiver: " + e.toString())
        }
    }


    fun startGameActivity(info: WifiP2pInfo) {
        println("I'M TRYING TO START THE GAME")
        val intent = Intent(this@WifiPeerSetupActivity, GameActivity::class.java).apply {
            this.putExtra("IS_OWNER", info.isGroupOwner)
            this.putExtra("SERVER_ADDRESS", info.groupOwnerAddress)
        }

        println("ABOUT TO START ACTIVITY")
        startActivity(intent)
    }
}
