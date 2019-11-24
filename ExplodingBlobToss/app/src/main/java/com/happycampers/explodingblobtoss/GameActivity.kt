package com.happycampers.explodingblobtoss

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.android.diceroller.ShakeDetector
import com.happycampers.explodingblobtoss.Hosts.P2PClient
import com.happycampers.explodingblobtoss.Hosts.P2PServer
import java.lang.NullPointerException
import java.net.InetAddress

class GameActivity : AppCompatActivity() {
    private lateinit var shakeDetector: ShakeDetector
    private var accelerometerSupported = false
    private var deviceIsOwner: Boolean? = null
    private var serverAddress: InetAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        shakeDetector = ShakeDetector(this)

        checkShakeDetectorSupported()

        val intent = getIntent()
        deviceIsOwner = intent.getBooleanExtra("IS_OWNER", false)
        serverAddress = intent.getSerializableExtra("SERVER_ADDRESS") as InetAddress

        serverAddress ?: run {
            Log.e("GameActivity", "Could not obtain server address from intent.")
        }

    }

    override fun onResume() {
        super.onResume()

        checkShakeDetectorSupported()

        shakeDetector.startListening(object: ShakeDetector.ShakeListener {
            override fun onShake(force: Float) {
                println(force)

                if (deviceIsOwner!!) {
                    P2PServer.Companion.ServerMessageTransferTask(serverAddress!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }
                else {
                    P2PClient.Companion.ClientMessageTransferTask(serverAddress!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        shakeDetector.stopListening()
    }


    //TODO: add start button that sends starting message to both devices (?)
    private fun checkShakeDetectorSupported() {
        if (!shakeDetector.isSupported()) {
            accelerometerSupported = false
            findViewById<TextView>(R.id.accelerometer_unsupported_warning).visibility = View.VISIBLE
        }
        else {
            accelerometerSupported = true
            findViewById<TextView>(R.id.accelerometer_unsupported_warning).visibility = View.GONE
        }
    }

    private fun countDown() {}

}
