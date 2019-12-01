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
import java.lang.NullPointerException
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
    private lateinit var audioSwitch:Switch
    private lateinit var hapticSwitch: Switch
    companion object {
        var deviceState: DeviceP2PListeningState = DeviceP2PListeningState.UNDEFINED
        var turnsLeft: Int = -1
        lateinit var throwAnimation:Animation
        lateinit var  catchAnimation:Animation
        lateinit var blob:ImageView

        var score: Int = 0
        var roundNumber: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val pauseButton = findViewById<ImageView>(R.id.pause_btn)
        val pauseMenu = findViewById<FrameLayout>(R.id.pause_menu)
        val resumeButton = findViewById<Button>(R.id.resume_button)
        val quitButton = findViewById<Button>(R.id.quit_button)

        guideArrow = findViewById<ImageView>(R.id.front_arrow)

        gameOverSplat = MediaPlayer.create(this,R.raw.game_over_splat)
        gameOverSplat.setVolume(0.1f,0.1f)
        throwSplat = MediaPlayer.create(this,R.raw.throw_splash)
        throwSplat.setVolume(0.1f,0.1f)
        blob = findViewById(R.id.blob_ImageView)
        instructionText = findViewById(R.id.instructions)
        shakeDetector = ShakeDetector(this)
        audioSwitch = findViewById(R.id.audio_switch)
        hapticSwitch = findViewById(R.id.haptic_switch)

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
            pauseMenu.visibility = View.INVISIBLE
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
            turnsLeft = Random().nextInt(11) + 6
            deviceState = DeviceP2PListeningState.SENDING
            blob.visibility = View.VISIBLE
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

    }

    override fun onStart() {
        super.onStart()

        findViewById<TextView>(R.id.round_count_lbl).text = "Round: $roundNumber"
        findViewById<TextView>(R.id.score_lbl).text = "Score: $score"
    }

    override fun onResume() {
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
                        blob.clearAnimation()

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
                        blob.clearAnimation()
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
        blob.startAnimation(throwAnimation)
        blob.visibility = View.INVISIBLE
        instructionText.text = "Catch the blob from your opponent!"
        if(audioSwitch.isChecked){
            throwSplat.start()
        }
        if (turnsLeft == 0) {
            score++
            roundNumber++

            deviceState = DeviceP2PListeningState.FINISHED
            startGameEndActivity(true, "You win!")
        }
        else {
            deviceState = DeviceP2PListeningState.RECEIVING
        }
    }

    fun catchBlob(turnCount: Int){
        if(audioSwitch.isChecked){
            throwSplat.start()
        }
        if(hapticSwitch.isChecked){
            vibratePhone(100)
        }

        blob.visibility = View.VISIBLE
        blob.startAnimation(catchAnimation)
        guideArrow.visibility = View.VISIBLE
        instructionText.text = "Pass the blob before it explodes!"
        instructionText.visibility = View.VISIBLE

        if (0 == turnCount) {
            roundNumber++
            if(audioSwitch.isChecked){
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
