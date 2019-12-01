package com.happycampers.explodingblobtoss

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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
        lateinit var throwAnimation:Animation
        lateinit var  catchAnimation:Animation
        lateinit var blob:ImageView

        fun throwBlob(){
            blob.startAnimation(throwAnimation)
            blob.visibility = View.INVISIBLE
            deviceState = DeviceP2PListeningState.RECEIVING
            turnsLeft--
        }
        fun catchBlob(result:String?){
            blob.visibility = View.VISIBLE
            blob.startAnimation(catchAnimation)
            deviceState = DeviceP2PListeningState.SENDING
            turnsLeft = result!!.split(" ", ignoreCase = true, limit = 0)[0].toInt()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val pauseButton = findViewById<ImageView>(R.id.pause_btn)
        val pauseMenu = findViewById<FrameLayout>(R.id.pause_menu)
        val resumeButton = findViewById<Button>(R.id.resume_button)
        val quitButton = findViewById<Button>(R.id.quit_button)
        throwAnimation= AnimationUtils.loadAnimation(this,R.anim.throw_blob)
        catchAnimation = AnimationUtils.loadAnimation(this,R.anim.catch_blob)
        blob = findViewById(R.id.blob_ImageView)


        shakeDetector = ShakeDetector(this)

//Pause Button
        pauseButton.setOnClickListener {
            pauseButton.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            pauseMenu.visibility = View.VISIBLE
            onPause()
        }
//Resume Button
        resumeButton.setOnClickListener {
            resumeButton.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            pauseMenu.visibility = View.GONE;
            onResume()
        }
//quit to menu
        quitButton.setOnClickListener {
            quitButton.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            val intent = Intent(this,MainActivity::class.java).apply {
                //?
            }
            startActivity(intent)
        }

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
