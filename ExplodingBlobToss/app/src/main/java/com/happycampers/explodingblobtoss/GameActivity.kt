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
import java.lang.ref.WeakReference
import java.net.InetAddress
import java.util.*

class GameActivity : AppCompatActivity() {
    private lateinit var shakeDetector: ShakeDetector
    private var accelerometerSupported = false
    private var deviceIsOwner: Boolean? = null
    private var serverAddress: InetAddress? = null

    companion object {
        var deviceState: DeviceP2PListeningState = DeviceP2PListeningState.UNDEFINED
        var turnsLeft: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        shakeDetector = ShakeDetector(this)

        checkShakeDetectorSupported()

        val intent = getIntent()
        deviceIsOwner = intent.getBooleanExtra("IS_OWNER", false)
        serverAddress = intent.getSerializableExtra("SERVER_ADDRESS") as InetAddress

        if (deviceIsOwner!!) {
            turnsLeft = Random().nextInt(11 + 5)
            deviceState = DeviceP2PListeningState.SENDING

            P2PServer.Companion.MessageServerAsyncTask(WeakReference(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
        else {
            Log.d("GameActivity", "Starting receiving socket on client for first turn from server.")

            P2PClient.Companion.ClientMessageReceiveTask(WeakReference(this)).execute(serverAddress)

            deviceState = DeviceP2PListeningState.RECEIVING
        }

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

                Log.d("GameActivity", "THIS IS THE DEVICE STATE: " + deviceState.toString())
                if (deviceState == DeviceP2PListeningState.SENDING) {
                    if (deviceIsOwner!!) {
                        P2PServer.Companion.StartServerForTransferTask().execute()
                        P2PServer.Companion.ServerMessageTransferTask(serverAddress!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, turnsLeft)//execute(turnsLeft) //OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

                        deviceState = DeviceP2PListeningState.RECEIVING
                        P2PServer.Companion.MessageServerAsyncTask(WeakReference(this@GameActivity)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) //TODO: see if this is safe. Want to let send finish first
                    }
                    else {
                        P2PClient.Companion.ClientMessageTransferTask(serverAddress!!).execute(turnsLeft) //(AsyncTask.THREAD_POOL_EXECUTOR)

                        deviceState = DeviceP2PListeningState.RECEIVING
                        P2PClient.Companion.ClientMessageReceiveTask(WeakReference(this@GameActivity)).execute(serverAddress)
                    }
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
