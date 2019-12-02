package com.happycampers.explodingblobtoss

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.happycampers.explodingblobtoss.Hosts.P2PClient
import com.happycampers.explodingblobtoss.Hosts.P2PServer
import kotlinx.android.synthetic.main.activity_game.*
import java.lang.ref.WeakReference
import java.net.InetAddress
import java.util.*

//sound clip attribution: from freesound.org FoolBoy Media(gameOver_splat), Green couch (throw_Splash)
class GameActivity : AppCompatActivity() {
    private val TAG = "GAMEACTIVITY"

    private lateinit var shakeDetector: ShakeDetector
    private var accelerometerSupported = false
    private var deviceIsOwner: Boolean? = null
    private var serverAddress: InetAddress? = null
    private lateinit var guideArrow:ImageView
    private lateinit var instructionText:TextView
    private lateinit var gameOverSplat:MediaPlayer
    private lateinit var throwSplat:MediaPlayer
    companion object {
        var deviceState: DeviceP2PListeningState = DeviceP2PListeningState.UNDEFINED
        var turnsLeft: Int = -1
        lateinit var throwAnimation:Animation
        lateinit var  catchAnimation:Animation
        var score: Int = 0
        var roundNumber: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        guideArrow = findViewById<ImageView>(R.id.front_arrow)

        gameOverSplat = MediaPlayer.create(this,R.raw.game_over_splat)
        gameOverSplat.setVolume(0.1f,0.1f)
        throwSplat = MediaPlayer.create(this,R.raw.throw_splash)
        throwSplat.setVolume(0.1f,0.1f)
        instructionText = findViewById(R.id.instructions)
        shakeDetector = ShakeDetector(this)

        throwAnimation= AnimationUtils.loadAnimation(this,R.anim.throw_blob)
        catchAnimation = AnimationUtils.loadAnimation(this,R.anim.catch_blob)

        catchAnimation.setAnimationListener(object: Animation.AnimationListener {
            //TODO: might want own animation listener object class
            override fun onAnimationEnd(p0: Animation?) {
                if (deviceState == DeviceP2PListeningState.TURN_PROCESSING) {
                    deviceState = DeviceP2PListeningState.FINISHED
                    startGameEndActivity(false, "You dropped the blob!")

                    if (deviceIsOwner!!) {
                        P2PServer.Companion.StartServerForTransferTask().execute()
                        P2PServer.Companion.ServerMessageTransferTask(serverAddress!!,WeakReference(this@GameActivity)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, -2)
                    }
                    else {
                        P2PClient.Companion.ClientMessageTransferTask(serverAddress!!, WeakReference(this@GameActivity)).execute(-2)
                    }
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationStart(p0: Animation?) {
                Log.d(TAG, "In onAnimationStart.")
            }
        })
        throwAnimation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                if (turnsLeft == 0) {
                    score++
                    roundNumber++

                    deviceState = DeviceP2PListeningState.FINISHED
                    startGameEndActivity(true, "You win!\nThe blob has exploded on your opponent.")
                }
                else {
                    deviceState = DeviceP2PListeningState.RECEIVING
                }
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                Log.d(TAG, "In onAnimationStart.")
            }
        })


        //Pause Button
        pause_btn.setOnClickListener {
            pause_btn.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            pause_menu.visibility = View.VISIBLE
            onPause()
        }
        //Resume Button
        resume_button.setOnClickListener {
            resume_button.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            pause_menu.visibility = View.INVISIBLE
            onResume()
        }
        //quit to menu
        quit_button.setOnClickListener {
            quit_button.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            val intent = Intent(this,MainActivity::class.java).apply {
                //?
            }
            startActivity(intent)
        }

        checkShakeDetectorSupported()
    }

    override fun onResume() {
        val intent = getIntent()
        deviceIsOwner = intent.getBooleanExtra("IS_OWNER", false)
        serverAddress = intent.getSerializableExtra("SERVER_ADDRESS") as InetAddress

        if (deviceIsOwner!!) {
            turnsLeft = Random().nextInt(11) + 6

            Log.d(TAG, "THERE WILL BE $turnsLeft TURNS")

            deviceState = DeviceP2PListeningState.SENDING
            blob_ImageView.visibility = View.VISIBLE
            guideArrow.visibility = View.VISIBLE
            instructionText.text = "Pass the blob before it explodes!"
            instructionText.visibility = View.VISIBLE

            P2PServer.Companion.StartServerForTransferTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

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

        super.onResume()

        findViewById<TextView>(R.id.round_count_lbl).text = "Round: $roundNumber"
        findViewById<TextView>(R.id.score_lbl).text = "Score: $score"

        checkShakeDetectorSupported()

        shakeDetector.startListening(object: ShakeDetector.ShakeListener {
            override fun onShake(force: Float, x: Float, y: Float, z: Float) {
                Log.d("GameActivity", "THIS IS THE DEVICE STATE: " + deviceState.toString())

                if (deviceState == DeviceP2PListeningState.SENDING) {
                    if (x > 0) {
                        catchAnimation.cancel()
                        blob_ImageView.clearAnimation()

                        if (deviceIsOwner!!) {
                            P2PServer.Companion.StartServerForTransferTask().execute()
                            P2PServer.Companion.ServerMessageTransferTask(
                                serverAddress!!,
                                WeakReference(this@GameActivity)
                            ).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR,
                                turnsLeft
                            )//execute(turnsLeft) //OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

                            deviceState = DeviceP2PListeningState.RECEIVING
                            P2PServer.Companion.MessageServerAsyncTask(WeakReference(this@GameActivity))
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) //TODO: see if this is safe. Want to let send finish first
                        } else {
                            P2PClient.Companion.ClientMessageTransferTask(
                                serverAddress!!,
                                WeakReference(this@GameActivity)
                            ).execute(turnsLeft) //(AsyncTask.THREAD_POOL_EXECUTOR)

                            deviceState = DeviceP2PListeningState.RECEIVING
                            P2PClient.Companion.ClientMessageReceiveTask(WeakReference(this@GameActivity))
                                .execute(serverAddress)
                        }
                    }
                }

                else if (deviceState == DeviceP2PListeningState.TURN_PROCESSING) {
                    if (x > 0) {
                        catchAnimation.cancel()
                        blob_ImageView.clearAnimation()
                        deviceState = DeviceP2PListeningState.SENDING
                    }

                    println("THIS IS THE DEVICE STATE (2): " + deviceState)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()

        shakeDetector.stopListening()
    }

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

    fun startGameEndActivity(isWinner: Boolean, endReason: String) {
        val intent = Intent(this@GameActivity, GameEndActivity::class.java).apply {
            this.putExtra("IS_OWNER", deviceIsOwner)
            this.putExtra("IS_WINNER", isWinner)
            this.putExtra("SERVER_ADDRESS", serverAddress)
            this.putExtra("END_REASON", endReason)
        }

        startActivity(intent)
    }

    fun throwBlob(){
        guideArrow.visibility = View.INVISIBLE
        blob_ImageView.startAnimation(throwAnimation)
        blob_ImageView.visibility = View.INVISIBLE
        instructionText.text = "Catch the blob from your opponent!"
        if(audio_switch.isChecked){
            throwSplat.start()
        }

    }

    fun catchBlob(turnCount: Int){
        if(audio_switch.isChecked){
            throwSplat.start()
        }
        if(haptic_switch.isChecked){
            vibratePhone(100)
        }

        blob_ImageView.visibility = View.VISIBLE
        blob_ImageView.startAnimation(catchAnimation)
        guideArrow.visibility = View.VISIBLE
        instructionText.text = "Pass the blob before it explodes!"
        instructionText.visibility = View.VISIBLE

        if (0 == turnCount) {
            roundNumber++
            if(audio_switch.isChecked){
                gameOverSplat.start()
            }
            deviceState = DeviceP2PListeningState.FINISHED
            startGameEndActivity(false, "The blob exploded!\nYou lose!")
        }
        else {
            deviceState = DeviceP2PListeningState.TURN_PROCESSING
            turnsLeft = (turnCount - 1)
        }
    }

    fun vibratePhone(length:Long) {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(length)
        }
    }

}
